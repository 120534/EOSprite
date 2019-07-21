package cn.geosprite.eosprocess.service

import astraea.spark.rasterframes._
import astraea.spark.rasterframes.ml.NoDataFilter
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import org.apache.spark.SparkContext
import org.apache.spark.sql.{ColumnName, DataFrame, SparkSession}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import cn.geosprite.eosprocess.utils.Utils.{readTiff, _}
import geotrellis.raster.render.ColorMap
import geotrellis.raster.{IntConstantNoDataCellType, MultibandTile, Tile}
import cn.geosprite.eosprocess.index.Indexes._
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel, LinearRegressionTrainingSummary}
import org.apache.spark.sql.functions.udf

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
    val bandNums = List(5, 4)
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
    log.info("开始进行真彩色合成 = {}", inputPath)
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
    val previewPath = arrPng.mkString("/") + "/" + previewName

    MultibandTile(redTile, greenTile, blueTile).equalize().mapBands((i: Int, tile:Tile) => {
      tile.rescale(0, 255)
    }).renderPng().write(previewPath)
    log.info("真彩色合成已经完成，输出路径 = {}", previewPath)
    previewName
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

  def doDDI(inputPath: String, outputPathPng: String, outputPathTiff:String)={
    val bandNums = (1 to 7).toList
    doDDIAN(inputPath, bandNums, outputPathPng, outputPathTiff,ddi_colorMap)
  }

  def doDDIAN(inputPath: String,
          bandNums: List[Int] ,
          outputPathPng: String,
          outputPathTiff:String,
          colorMap: ColorMap)={

    implicit val ss: SparkSession = spark.withRasterFrames
    import ss.implicits._
    log.info("开始进行荒漠化分类 ={}",inputPath)
    //根据路径找到对应的sr影像
    val paths = findTiffPath(inputPath)

    val joinedRF = bandNums.
      map { b => (b, readTiff(paths(b))) }.
      map { case (b, t) => t.projectedRaster.toRF(s"tile_$b") }.
      reduce(_ spatialJoin _)
    log.info("数据读取完毕 ={}",inputPath)

    val metadata = joinedRF.tileLayerMetadata.left.get

    /**
      * 提取沙地信息
      * 1.计算反射率
      * 2.使用Kmeans分类，设置为6类，数值最大的即为沙地，features为6.
      * */

    /**计算得到sand波段信息*/
    val sandRF:DataFrame = joinedRF.withColumn("sand", convert_cell_type(sand($"tile_2", $"tile_3", $"tile_4", $"tile_5", $"tile_6", $"tile_7"), "float32"))

    //calculate ndvi
    val ndviRF: DataFrame = sandRF.withColumn("ndvi",
      normalize(normalized_difference($"tile_5",$"tile_4")))

    //calculate albedo
    val albedoRF: DataFrame = ndviRF.withColumn("albedo",
      normalize(albedo($"tile_2", $"tile_4", $"tile_5",$"tile_6", $"tile_7")))

    log.info("指数计算完毕 {}",outputPathPng)
    // Similarly pull in the target label data.
    val feature = "feature" //ndvi
    val label = "label" //albedo
    val newRF: DataFrame = albedoRF.select( $"spatial_key", $"ndvi", $"albedo",$"sand")

    /**对RasterFrame中的col重命名*/
    val trainRF: DataFrame = newRF.withColumnRenamed("ndvi", "feature")
      .withColumnRenamed("albedo", "label")

    import astraea.spark.rasterframes.ml.TileExploder
    /**Tile转换为多个row的像元值*/
    val exploderSand = new TileExploder()

    val assemberSand = new VectorAssembler().setInputCols(Array("sand")).setOutputCol("sandFeature")
    /**过滤空值*/
    val noDataFilterSand: NoDataFilter = new NoDataFilter()
      .setInputCols(Array("sand"))

    /**设置六个类别*/
    val kmeans = new KMeans()
      .setK(6)
      .setMaxIter(3)
      .setPredictionCol("sandPrediction")
      .setFeaturesCol("sandFeature")

    /**创建工作流*/
    val pipelineSand = new Pipeline().setStages(Array(exploderSand, noDataFilterSand, assemberSand, kmeans))

    /**执行Estimator*/
    val modelSand = pipelineSand.fit(trainRF)

    /**获取中间*/
    val sandModel = modelSand.stages.collect{ case km: KMeansModel ⇒ km}.head
    /**执行Transformer*/
    val resultSand = modelSand.transform(trainRF)

    /**
      * 需要将sand聚类最大的定义为sand
      *
      * */

    val arrSand = sandModel.clusterCenters.flatMap(_.toArray)
    val maxNum = arrSand.indexOf(arrSand.max)

    /**
      * 将sand设置为200
      * 将water设置为220
      *
      * */

    val func2 = udf{d:Int =>
      if (d == maxNum) 200
      else d
    }

    val kmSand = resultSand
      .withColumn("sandPrediction", func2($"sandPrediction"))
      .drop("sand", "sandFeature")

    log.info("沙地信息提取完毕：{}",outputPathPng)

    val colNames = Array("label", "feature")
    val noDataFilter: NoDataFilter = new NoDataFilter()
      .setInputCols(colNames)

    // To "vectorize" the the band columns we use the SparkML `VectorAssembler`
    val assembler: VectorAssembler = new VectorAssembler()
      .setInputCols(Array("feature"))
      .setOutputCol("features")

    // Using a decision tree for classification
    val classifier: LinearRegression = new LinearRegression()
      .setMaxIter(2)
      .setElasticNetParam(0.8)

    // Assemble the model pipeline
    val pipeline: Pipeline = new Pipeline()
      .setStages(Array(noDataFilter, assembler, classifier))

    val lrModel: PipelineModel = pipeline.fit(kmSand)

    val result: DataFrame = lrModel.transform(kmSand)
    result.show(10)
    val stage3: LinearRegressionModel = lrModel.stages.collect{ case lr: LinearRegressionModel ⇒ lr}.head
    val coefficient = stage3.coefficients
    log.info(s"Coefficients: ${stage3.coefficients} Intercept: ${stage3.intercept}")
    val trainingSummary: LinearRegressionTrainingSummary = stage3.summary
    log.info(s"numIterations: ${trainingSummary.totalIterations}")
    log.info(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    log.info(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    log.info(s"r2: ${trainingSummary.r2}")

    /**
      * 直接基于线性回归结果进行处理，feature为ndvi，label为albedo
      * */

    import org.apache.spark.sql.functions.lit
    /**创建一列为固定值使用lit() */
    val result1 = result.withColumn("coefficient",lit(coefficient.apply(0)))
    val ddiCalculator = (ndvi:Double, albedo:Double, coefficient:Double) => {
      (-1/coefficient)*ndvi - albedo
    }

    val ddiUdf = udf(ddiCalculator)

    val ddiRF = result1.withColumn("ddi",ddiUdf($"feature",$"label",$"coefficient"))
      .drop("feature", "label", "features", "prediction", "coefficient")

    log.info("ddi计算完毕，开始进行荒漠化分级")

    /**可替换为Jenks*/
    val kmeansDDI = new KMeans().setK(5).setMaxIter(3)
    val assembler1: VectorAssembler = new VectorAssembler()
      .setInputCols(Array("ddi"))
      .setOutputCol("features")
    val pipeline2 = new Pipeline().setStages(Array(assembler1, kmeansDDI))
    val model2 = pipeline2.fit(ddiRF)
    val result2 = model2.transform(ddiRF)

    val kmeansModel =  model2.stages.collect{ case km: KMeansModel ⇒ km}.head
    val summary = kmeansModel.summary
    val arr = kmeansModel.clusterCenters.flatMap(x => x.toArray)

    /**
      * 需要将荒漠化的程度对应的DDI按照 0 - 4排列
      *
      * */
    val map1 = centerToIndex(arr)

    val func1 = udf{d:Int => map1.getOrElse(d, Int.MinValue)}

    val rectified = result2.withColumn("desertResult",func1($"prediction"))

    val func3 = udf{(x:Int, y:Int) => if(y == 200) y else x}

    /**将沙地标记出来*/
    val detectedSand = rectified.withColumn("sandLabel",func3($"desertResult", $"sandPrediction"))

    val tlm = joinedRF.tileLayerMetadata.left.get

    val retiled = detectedSand.groupBy($"spatial_key").agg(
      assemble_tile(
        $"column_index", $"row_index", $"sandLabel",
        tlm.tileCols, tlm.tileRows, IntConstantNoDataCellType
      )
    )
    val rf = retiled.asRF($"spatial_key", tlm)

    val cols = tlm.layout.tileLayout.totalCols.toInt
    val rows = tlm.layout.tileLayout.totalRows.toInt
    val raster = rf.toRaster($"sandLabel", cols, rows)
    //创建输出文件目录 LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11/LC81170502019011LGN00.TIF
    val arrPng = outputPathPng.split("/")
    val dirPng = arrPng.take(arrPng.length - 1).mkString("/")

    val arrTiff = outputPathTiff.split("/")
    val dirTiff = arrTiff.take(arrTiff.length - 1).mkString("/")

    mkdir(dirPng)
    mkdir(dirTiff)

    //输出png图片
    log.info("output ddi png image result to {}",outputPathPng)
    raster.tile.renderPng(colorMap).write(outputPathPng)

    //输出tiff文件
    log.info("output ddi tiff image result to {}",outputPathTiff)
    SinglebandGeoTiff(raster, metadata.extent, metadata.crs).write(outputPathTiff)
  }
}