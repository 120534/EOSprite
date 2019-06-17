package cn.geosprite.eosprocess.service

import astraea.spark.rasterframes._
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import org.apache.spark.SparkContext
import org.apache.spark.sql.{ColumnName, DataFrame, SparkSession}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import cn.geosprite.eosprocess.utils.Utils._
import geotrellis.raster.render.ColorMap
import cn.geosprite.eosprocess.index.Indexes._
import geotrellis.raster.{MultibandTile, Tile}

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
  def doNDVI(inputPath: String, outputPathPng: String, outputPathTiff:String): Unit = {
    val bandNums = List(4, 5)
    doNDIndex(inputPath, bandNums, outputPathPng, outputPathTiff, ndvi_colorMap)
  }

  /**
    * NDWI = (绿波段 - 近红外波段) / (绿波段 + 近红外波段)
    * landsat8: NDWI = (band3 - band5) / (band3 + band5)
    */
  def doNDWI(inputPath: String, outputPathPng: String, outputPathTiff:String): Unit={
    val bandNums = List(3, 5)
    doNDIndex(inputPath, bandNums, outputPathPng, outputPathTiff, ndwi_colorMap)
  }

  def doTrueColorComposite(inputPath: String): String={
    val arr = findTiffPath(inputPath)

    /**各波段影像的路径*/
    val red = arr.apply(4)
    val green = arr.apply(3)
    val blue = arr.apply(2)

    val redTiff = SinglebandGeoTiff(red)
    val extent = redTiff.extent
    val crs = redTiff.crs

    val redTile = redTiff.tile
    val greenTile = SinglebandGeoTiff(green).tile
    val blueTile = SinglebandGeoTiff(blue).tile

    val arrPng = inputPath.split("/")
    val previewName = arrPng.last + ".png"
    val previewPath = arrPng.take(arrPng.length - 1).mkString("/") + "/" + previewName

    MultibandTile(redTile, greenTile, blueTile).equalize().mapBands((i: Int, tile:Tile) => {
      tile.rescale(0, 255)
    }).renderPng().write(previewPath)
    previewPath
  }

  //归一化建筑指数  (MIR-NIR)/(MIR+NIR)
  def doNDBI(): Unit ={

  }
  //  def getTGSI(blue:Tile,green:Tile,red:Tile): Unit ={
  //    (red - blue)/(red + blue + green)
  //  }
  def doTGSI(): Unit ={


  }
  //  def getMSAVI(red:Tile,nir:Tile):Tile = {
  //    (nir * 2 + 1 - Abs(Sqrt((nir * 2 + 1) * (nir * 2 + 1) - (nir - red) * 8))) / 2
  //    //  （2*float(b5)+1-abs(sqrt((2*float(b5)+1)*(2*float(b5)+1)-8*(float(b5)-float(b4))))/2
  //  }
  def doMSAVI(): Unit ={

  }

  def doAlbedo(rasterFrame: RasterFrame,list: List[ColumnName]):Unit ={
  }

  /**
    *a common method to get normal difference index,suitable for XXX = (band(x) - band(y)) / (band(x) + band(y))
    * @Param band(Int, Int), the first param is for the head band, and the second param is for the second band
    *       , be careful with the sequence.
    */
  def doNDIndex(inputPath: String,
                 bandNums: List[Int] ,
                 outputPathPng: String,
                 outputPathTiff:String,
                 colorMap: ColorMap
                ):Unit = {
    if (bandNums.length != 2){
      throw new AssertionError("Calculating NDIndex with bandNums count less than two")
    }

    implicit val ss: SparkSession = spark.withRasterFrames
    import ss.implicits._
    log.info("Starting calculating normal difference index for image with path ={}",inputPath)
    //根据路径找到对应的sr影像
    val paths = findTiffPath(inputPath)

    val joinedRF = bandNums.
      map { b => (b, readTiff(paths(b))) }.
      map { case (b, t) => t.projectedRaster.toRF(s"band_$b") }.
      reduce(_ spatialJoin _)

    val metadata = joinedRF.tileLayerMetadata.left.get
    val tlm = metadata.tileLayout

    val bands:List[ColumnName] = bandNums.map(toColumn)

    val cn1:ColumnName = bands.head
    val cn2:ColumnName = bands.last

    val rf = joinedRF.withColumn("index",
      normalized_difference(convert_cell_type(cn1, "float32"),
        convert_cell_type(cn2, "float32"))).asRF

    val raster = rf.toRaster($"index", tlm.totalCols.toInt, tlm.totalRows.toInt)

    //创建输出文件目录 LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11/LC81170502019011LGN00.TIF
    val arrPng = outputPathPng.split("/")
    val dirPng = arrPng.take(arrPng.length - 1).mkString("/")

    val arrTiff = outputPathTiff.split("/")
    val dirTiff = arrTiff.take(arrTiff.length - 1).mkString("/")

    mkdir(dirPng)
    mkdir(dirTiff)

    //输出png图片
    log.info("output ndvi png image result to {}",outputPathPng)
    raster.tile.renderPng(colorMap).write(outputPathPng)

    //输出tiff文件
    log.info("output ndvi tiff image result to {}",outputPathTiff)
    SinglebandGeoTiff(raster, metadata.extent, metadata.crs).write(outputPathTiff)

  }
}