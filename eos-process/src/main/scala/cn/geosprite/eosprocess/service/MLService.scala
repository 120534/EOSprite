package cn.geosprite.eosprocess.service

import java.io.PrintWriter
import astraea.spark.rasterframes._
import astraea.spark.rasterframes.ml.NoDataFilter
import astraea.spark.rasterframes.{RasterFrame, cell_type, convert_cell_type, normalized_difference}
import cn.geosprite.eosprocess.index.Indexes._
import geotrellis.raster.io.geotiff.{MultibandGeoTiff, SinglebandGeoTiff}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel, LinearRegressionTrainingSummary}
import org.apache.spark.sql.{ColumnName, DataFrame, SparkSession}
import cn.geosprite.eosprocess.utils.Utils.{findTiffPath, mkdir, readTiff, toColumn}
import geotrellis.raster.render.{ColorMap, ColorRamps, IndexedColorMap}
import geotrellis.raster.{IntConstantNoDataCellType, MultibandTile, Tile}
import org.apache.spark.SparkContext
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 20:36 2019-5-29
  * @ Description：Machine Learning Service For RS Data
  * @ Modified By：
  */

@Service
class MLService {

  private val log :org.slf4j.Logger  = org.slf4j.LoggerFactory.getLogger(classOf[MLService])

  @Autowired
  private var sc: SparkContext = _

  @Autowired
  private var spark: SparkSession = _

//  def doAlbedoNdvi(
//                    inputPath: String,
//                    bandNums: List[Int] ,
//                    outputPathPng: String,
//                    outputPathTiff:String,
//                    colorMap: ColorMap
//                  ): Unit ={
//    implicit val spark: SparkSession = spark.withRasterFrames
//    import spark.implicits._
//
//    // The first step is to load multiple bands of imagery and construct
//    // a single RasterFrame from them.
//
//    log.info("Start creating feature space for image in folder = {}",inputPath)
//    //根据路径找到对应的sr影像
//    val paths = findTiffPath(inputPath)
//
//    val joinedRF = bandNums.
//      map { b => (b, readTiff(paths(b))) }.
//      map { case (b, t) => t.projectedRaster.toRF(s"tile_$b") }.
//      reduce(_ spatialJoin _)
//
//    val metadata = joinedRF.tileLayerMetadata.left.get
//
//    // We should see a single spatial_key column along with 4 columns of tiles.
//    joinedRF.printSchema()
//    val bands: Range.Inclusive = 2 to 7
//    val rawNames: Array[String] = bands.map(b => s"tile_$b").toArray
//
//    //calculate ndvi
//    val ndviRF: DataFrame = joinedRF.withColumn("ndvi",
//      normalize(normalized_difference($"tile_5",$"tile_4")))
//
//    //calculate msavi, [red, nir]
//    val albedoRF: DataFrame = ndviRF.withColumn("albedo",
//      normalize(albedo($"tile_2", $"tile_4", $"tile_5",$"tile_6", $"tile_7")))
//
//    albedoRF.select(cell_type($"albedo")).show()
//    // Similarly pull in the target label data.
//    val feature = "feature" //ndvi
//    val label = "label" //albedo
//    val newRF: DataFrame = albedoRF.select( $"spatial_key", $"ndvi", $"albedo")
//
//    /**对RasterFrame中的col重命名*/
//    val trainRF: DataFrame = newRF.withColumnRenamed("ndvi", "feature")
//      .withColumnRenamed("albedo", "label")
//
//    import astraea.spark.rasterframes.ml.TileExploder
//    val exploder = new TileExploder()
//
//    val colNames = Array("label", "feature")
//    val noDataFilter: NoDataFilter = new NoDataFilter()
//      .setInputCols(colNames)
//
//    // To "vectorize" the the band columns we use the SparkML `VectorAssembler`
//    val assembler: VectorAssembler = new VectorAssembler()
//      .setInputCols(Array("feature"))
//      .setOutputCol("features")
//
//    // Using a decision tree for classification
//    val classifier: LinearRegression = new LinearRegression()
//      .setMaxIter(10)
//      .setElasticNetParam(0.8)
//
//    // Assemble the model pipeline
//    val pipeline: Pipeline = new Pipeline()
//      .setStages(Array(exploder, noDataFilter, assembler, classifier))
//
//    val lrModel: PipelineModel = pipeline.fit(trainRF)
//    val result: DataFrame = lrModel.transform(trainRF)
//
//    val stage3: LinearRegressionModel = lrModel.stages(3).asInstanceOf[LinearRegressionModel]
//    val coefficient = stage3.coefficients
//
//    log.info(s"Coefficients: ${stage3.coefficients} Intercept: ${stage3.intercept}")
//    val trainingSummary: LinearRegressionTrainingSummary = stage3.summary
//    log.info(s"numIterations: ${trainingSummary.totalIterations}")
//    log.info(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
//    trainingSummary.residuals.show()
//    log.info(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
//    log.info(s"r2: ${trainingSummary.r2}")
//
//    /**
//      * 直接基于线性回归结果进行处理，feature为ndvi，label为albedo
//      * */
//
//    import org.apache.spark.sql.functions.lit
//    val result1 = result.withColumn("coefficient",lit(coefficient.apply(0)))
//
//
//
//    val ddiRF = result1.withColumn("ddi",ddi($"feature", $"label", $"coefficient"))
//      .drop("feature", "label", "features", "prediction", "coefficient")
//
//    val kmean = new KMeans().setK(5)
//    val assembler1: VectorAssembler = new VectorAssembler()
//      .setInputCols(Array("ddi"))
//      .setOutputCol("features")
//
//    val pipeline2 = new Pipeline().setStages(Array(assembler1, kmean))
//    val model2 = pipeline2.fit(ddiRF)
//    val result2 = model2.transform(ddiRF)
//    result2.show(10)
//
//    val kmeansModel = model2.stages.apply(1).asInstanceOf[KMeansModel]
//    val summary = kmeansModel.summary
//    val arr = kmeansModel.clusterCenters
//
//    val tlm = joinedRF.tileLayerMetadata.left.get
//
//    val retiled = result2.groupBy($"spatial_key").agg(
//      assemble_tile(
//        $"column_index", $"row_index", $"prediction",
//        tlm.tileCols, tlm.tileRows, IntConstantNoDataCellType
//      )
//    )
//    val rf = retiled.asRF($"spatial_key", tlm)
//
//    val cols = tlm.layout.tileLayout.totalCols.toInt
//    val rows = tlm.layout.tileLayout.totalRows.toInt
//    val raster = rf.toRaster($"prediction", cols, rows)
//
//    val arrPng = outputPathPng.split("/")
//    val dirPng = arrPng.take(arrPng.length - 1).mkString("/")
//
//    val arrTiff = outputPathTiff.split("/")
//    val dirTiff = arrTiff.take(arrTiff.length - 1).mkString("/")
//
//    mkdir(dirPng)
//    mkdir(dirTiff)
//
//    //输出png图片
//    log.info("output ndvi png image result to {}",outputPathPng)
//    raster.tile.renderPng(colorMap).write(outputPathPng)
//
//    //输出tiff文件
//    log.info("output ndvi tiff image result to {}",outputPathTiff)
//    SinglebandGeoTiff(raster, metadata.extent, metadata.crs).write(outputPathTiff)
//
//    def writeLog(str:String ): Unit ={
//      val out = new PrintWriter(outputPathTiff + "/modelMetadata.log")
//      out.write(summary.toString)
//      out.close()
//  }
//
//    spark.stop()
//  }

}
