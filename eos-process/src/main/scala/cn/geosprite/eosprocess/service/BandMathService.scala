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

  /**
    * NDVI = (近红外波段 - 红波段) / (近红外波段 + 红波段)
    * Landsat8: NDVI = (band5 - band4) / (band5 + band4)
    * @param inputPath
    * @param outputPathPng
    * @param outputPathTiff
    */
  def getNDVI(inputPath: String, outputPathPng: String, outputPathTiff:String): Unit = {
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

  /**
    * NDWI = (绿波段 - 近红外波段) / (绿波段 + 近红外波段)
    * landsat8: NDWI = (band3 - band5) / (band3 + band5)
    */
  def getNDWI(inputPath: String, outputPathPng: String, outputPathTiff:String): Unit={
    implicit val ss: SparkSession = spark.withRasterFrames
    import ss.implicits._
    log.info("Starting calculating ndvi for image path {}",inputPath)
    //根据路径找到对应的sr影像
    val paths = findTiffPath(inputPath)
    val bandNums = List(3, 5)
    val band = List(3, 5)

    val joinedRF = bandNums.
      map { b => (b, readTiff(paths(b))) }.
      map { case (b, t) => t.projectedRaster.toRF(s"band_$b") }.
      reduce(_ spatialJoin _)

    val metadata = joinedRF.tileLayerMetadata.left.get
    val tlm = metadata.tileLayout

    val rf = joinedRF.withColumn("nwvi",
      normalized_difference(convert_cell_type($"band_3", "float32"),
        convert_cell_type($"band_5", "float32"))).asRF

    val raster_ndwi = rf.toRaster($"nwvi", tlm.totalCols.toInt, tlm.totalRows.toInt)

    //创建输出文件目录 LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11/LC81170502019011LGN00.TIF
    val arr = outputPathPng.split("/")
    val dir = arr.take(arr.length - 1).mkString("/")
    mkdir(dir)

    //输出png图片
    log.info("output ndvi png image result to {}",outputPathPng)
    raster_ndwi.tile.renderPng(ndwi_colorMap).write(outputPathPng)

    //输出tiff文件
    log.info("output ndvi tiff image result to {}",outputPathTiff)
    SinglebandGeoTiff(raster_ndwi, metadata.extent, metadata.crs).write(outputPathTiff)

  }
  //归一化建筑指数  (MIR-NIR)/(MIR+NIR)
  def getNDBI(): Unit ={


  }
//  def getTGSI(blue:Tile,green:Tile,red:Tile): Unit ={
//    (red - blue)/(red + blue + green)
//  }
  def getTGSI()={}

//  def getMSAVI(red:Tile,nir:Tile):Tile = {
//    (nir * 2 + 1 - Abs(Sqrt((nir * 2 + 1) * (nir * 2 + 1) - (nir - red) * 8))) / 2
//    //  （2*float(b5)+1-abs(sqrt((2*float(b5)+1)*(2*float(b5)+1)-8*(float(b5)-float(b4))))/2
//  }
  def getMSAVI(){}


}