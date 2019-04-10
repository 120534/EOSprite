package cn.geosprite.eosprocess.service

import java.io.File

import astraea.spark.rasterframes._
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import cn.geosprite.eosprocess.utils.Utils._

import scala.tools.nsc.io.Path

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 15:35 2019-3-22
  * @ Description：None
  * @ Modified By：
  */
@Service
class BandMathService {

  @Autowired
  private var sc: SparkContext = _

  @Autowired
  private var spark: SparkSession = _

  def getNdvi(inputPath: String): Unit = {
    //for example, the name is "L8-Elkton-VA.tiff"
    implicit val ss: SparkSession = spark.withRasterFrames
    import ss.implicits._

    //根据传入的影像名称，获取其每个波段对应的路径信息

    val outputDir = inputPath.replace("sr", "ndvi")

    //根据路径找到对应的sr影像
    val paths = findTiffPath(inputPath)

    val bandNums = (4 to 5).toList

    val joinedRF = bandNums.
      map { b => (b, readTiff(paths(b))) }.
      map { case (b, t) => t.projectedRaster.toRF(s"band_$b") }.
      reduce(_ spatialJoin _)

    val metadata = joinedRF.tileLayerMetadata.left.get
    val tlm = metadata.tileLayout

    val rf = joinedRF.withColumn("ndvi",
      normalized_difference(convert_cell_type($"band_5", "float32"),
        convert_cell_type($"band_4", "float32"))).asRF

    val raster_ndvi = rf.toRaster($"ndvi", tlm.totalCols.toInt, tlm.totalRows.toInt)

    //创建输出文件目录
    mkdir(outputDir)

    //输出png图片
    raster_ndvi.tile.renderPng(ndvi_colorMap).write(outPath(outputDir, "png"))

    //输出tiff文件
    SinglebandGeoTiff(raster_ndvi, metadata.extent, metadata.crs).write(outPath(outputDir, "tiff"))
  }
}