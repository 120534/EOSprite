package cn.geosprite.eosprocess.index

import geotrellis.raster._
import geotrellis.raster.mapalgebra.local.{Abs, Sqrt}
import lombok.extern.slf4j.Slf4j
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 21:02 2019-5-7
  * @ Description：None
  * @ Modified By：
  */
object Indexes {

  /**
    * description: 计算地表反照率，传入数据为地表反射率。调用了geotrellis提供的map algebra方法
    * created time:  2018/11/5
    *
    * Albedo=a*NDVI+b
    * label = a * feature + b
    * params [blue, red, nir, swir1, swir2]
    * band 2, 4, 5, 6, 7
    * @return _root_.geotrellis.raster.Tile
    */
  val albedo: UserDefinedFunction = udf((blue: Tile, red: Tile, nir: Tile, swir1: Tile, swir2: Tile) =>{
    blue * 0.356 + red * 0.13 + nir * 0.373 + swir1 * 0.085 + swir2 * 0.072 - 0.0018
  })

  /**
    * description:
    * created time:  2018/11/5
    *
    *  params [red, nir]
    * @return _root_.geotrellis.raster.Tile
    */
  val msavi: UserDefinedFunction = udf((red: Tile, nir: Tile) => {
    (nir * 2 + 1 - Abs(Sqrt((nir * 2 + 1) * (nir * 2 + 1) - (nir - red) * 8))) / 2
  })

  /**
    * description:
    * created time:  2018/11/5
    *
    *  params [blue, green, red]
    * @return Unit
    */
  val tgsi: UserDefinedFunction = udf((blue:Tile, green:Tile, red:Tile) => {
    (red - blue)/(red + blue + green)
  })

  /**
    * description: 对得到的指数进行归一化处理，Geotrellis也提供了 geotrellis.raster.Tile.normalize方法
    * created time:  2018/11/5
    *  内存溢出报错，需要进行重写。
    *  params [tile]
    * @return _root_.geotrellis.raster.Tile
    */
  val normalize: UserDefinedFunction = udf((tile: Tile) =>{
    val (min,max) = tile.findMinMaxDouble
    (tile - min)/(max - min)
  })

  /**
   * description:获取沙地信息，得到指数信息后，
    * 通过自然间断点对其分为六类，最大的一类为沙地。
   * function: float(b2)+float(b3)+float(b4)+float(b5)+float(b6)+float(b7)
   * created time:  2019-5-23
   *
   *  params
   * @return
   */
  val sandIndex:UserDefinedFunction = udf((tile: List[Tile]) => {
    if (tile.length != 7){
      throw new Exception(s"tile length for calculate sandIndex must ${tile.length}")
    }
    val tile1 = tile.head
    val tileRest = tile.drop(0)
    val sum = tileRest.foldLeft(tile1)(_+_)
    sum
  })

  /**
    * description:第一种方法
    * created time:  2018/11/30
    *
    *  params [ndvi]
    * @return _root_.geotrellis.raster.Tile
    */
  def getFVC(ndvi:Tile):Tile={
    val quantile = ndvi.histogram.quantileBreaks(20)
    val ndviSoil = quantile(1)
    val ndviVeg = quantile(quantile.length - 2)
    getFVC(ndvi, ndviSoil, ndviVeg)
  }

  /**
    * description:第二种方法
    * created time:  2018/11/30
    *
    *  params [ndvi, ndviSoil, ndviVeg]
    * @return _root_.geotrellis.raster.Tile
    */
  def getFVC(ndvi:Tile, ndviSoil:Double, ndviVeg:Double): Tile ={
    (ndvi - ndviSoil) / (ndviVeg - ndviSoil)
    //FVC = (NDVI-NDVIsoil)/(NDVIveg-NDVIsoil)
  }

  /**
     * description: 计算DDI
     * created time:  2019-5-16
     *
     *  params  a为ndvi-albedo拟合线性回归的斜率
     * @return
     */
  val ddi: UserDefinedFunction = udf((ndvi:Tile, albedo: Tile, coefficient:Double) => {
      ndvi*(-1 / coefficient) - albedo
  })
}
