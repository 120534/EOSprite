package cn.geosprite.eosprocess.service

import java.io.File

import astraea.spark.rasterframes._
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import cn.geosprite.eosprocess.utils.Utils._
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger

import scala.tools.nsc.io.Path

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 15:35 2019-3-22
  * @ Description：None
  * @ Modified By：
  */
@Service
class BandMathService {
  private val log :org.slf4j.Logger  = org.slf4j.LoggerFactory.getLogger(classOf[BandMathService])

  @Autowired
  private var sc: SparkContext = _

  @Autowired
  private var spark: SparkSession = _

  def getNdvi(inputPath: String, outputPathPng: String, outputPathTiff:String): Unit = {
    //for example, the name is "L8-Elkton-VA.tiff"
    implicit val ss: SparkSession = spark.withRasterFrames
    import ss.implicits._
    log.info("Starting calculating ndvi for image path {}",inputPath)
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

    //创建输出文件目录 LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11/LC81170502019011LGN00.TIF
    val arr = outputPathPng.split("/")
    val dir = arr.take(arr.length - 1).mkString("/")
    mkdir(dir)

    //输出png图片
    log.info("output ndvi png image result to {}",outputPathPng)
    raster_ndvi.tile.renderPng(ndvi_colorMap).write(outputPathPng)

    //输出tiff文件
    log.info("output ndvi tiff image result to {}",outputPathTiff)
    SinglebandGeoTiff(raster_ndvi, metadata.extent, metadata.crs).write(outputPathTiff)
  }
}