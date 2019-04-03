package cn.geosprite.eosprocess.service

import astraea.spark.rasterframes._
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import cn.geosprite.eosprocess.utils.Utils._

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

  def getNdvi(name: String):Unit = {
    //for example, the name is "L8-Elkton-VA.tiff"
    implicit val ss:SparkSession = spark.withRasterFrames
    import ss.implicits._

    //根据传入的影像名称，获取其每个波段对应的路径信息
    val seq = name.split("-").toList
    val newList = seq.head :: "B%d" :: seq.tail
    val filePattern = "data/" + newList.mkString("-")

    val bandNums = (4 to 5).toList

    val joinedRF = bandNums.
      map{ b =>(b, filePattern.format(b))}.
      map{ case (b, f) => (b, readTiff(f))}.
      map{case (b, t) => t.projectedRaster.toRF(s"band_$b")}.
      reduce( _ spatialJoin _)

    val metadata = joinedRF.tileLayerMetadata.left.get
    val tlm = metadata.tileLayout

    val rf = joinedRF.withColumn("ndvi",
      normalized_difference(convert_cell_type($"band_5", "float32"),
        convert_cell_type($"band_4", "float32"))).asRF

    val raster_ndvi = rf.toRaster($"ndvi", tlm.totalCols.toInt, tlm.totalRows.toInt)
    raster_ndvi.tile.renderPng(ndvi_colorMap).write(outPath(name, "png"))
    SinglebandGeoTiff(raster_ndvi, metadata.extent, metadata.crs).write(outPath(name, "tiff"))
  }
}