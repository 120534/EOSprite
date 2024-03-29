package cn.geosprite.eosprocess.utils

import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}

import com.typesafe.config.ConfigFactory
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.render.ColorMap
import javax.imageio.ImageIO
import org.apache.spark.sql.ColumnName

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 9:31 2019-3-25
  * @ Description：None
  * @ Modified By：
  */
object Utils {

  def readTiff(name: String): SinglebandGeoTiff = SinglebandGeoTiff(name)

  val ndvi_colorMap:ColorMap = ColorMap.fromStringDouble(ConfigFactory.load().getString("colorMap.ndvi")).get
  val ndwi_colorMap:ColorMap = ColorMap.fromStringDouble(ConfigFactory.load().getString("colorMap.ndwi")).get
  /**    荒漠化分为五类 【无荒漠化、轻度荒漠化、中度荒漠化、重度荒漠化、极重度荒漠化】
    *     无荒漠化 ： #c4e6c3   196,230,195     4
    *     轻度荒漠化：#ffdd9a    255,221,154   3
    *     中度荒漠化：#ffa679     255,166,121  2
    *     重度荒漠化：#a65461ff   166,84,97  1
    *     极重度荒漠化：#541f3fff  84,31,63 0
    *     沙地：#ffff00ff      255,255,0     200
    *     水系：#1e90ff             220
    * */
  val ddi_colorMap:ColorMap = ColorMap.fromString("0:541f3fff;1:a65461ff;2:ffa679ff;3:ffdd9aff;4:c4e6c3ff;200:ffff00ff;").get

  //设置输出路径
  //  path: /mnt/disk1/geodata/lc8/ndvi/117/043/LC81170432018184LGN00 t: tif
  //  renturn /mnt/disk1/geodata/lc8/ndvi/117/043/LC81170432018184LGN00/LC81170432018184LGN00.tif
  def outPath(path: String, t: String):String =  {
    val dataName = path.split("/").last
    path +"/" + dataName + "." + t
  }

  // image to Array[Byte]
  @throws[Exception]
  def fileToByte(img: File): Array[Byte] = {
    var bytes:Array[Byte] = null
    val baos = new ByteArrayOutputStream
    try {
      var bi: BufferedImage = null
      bi = ImageIO.read(img)
      ImageIO.write(bi, "png", baos)
      bytes = baos.toByteArray
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally baos.close()
    bytes
  }

  //  LC08_L1TP_121039_20180325_20180404_01_T1_sr_band1.tif
  /**   input a dir to find all sr data path. band${num} equals to the index of  output Array
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_aerosol.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band1.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band2.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band3.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band4.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band5.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band6.tif",
    * "/mnt/disk1/geodata/lc8/sr/117/043/LC81170432018184LGN00/LC08_L1TP_117043_20180703_20180717_01_T1_sr_band7.tif"
    * @param dir
    * @return
    */
  def findTiffPath(dir: String): Array[String] ={
      val file = new File(dir)
      val files = file.listFiles()
        //过滤所有目录
        .filter(! _.isDirectory)
        .map(x => x.toString)
        //过滤得到.tif
        .filter(t => t.endsWith(".tif"))
        //过滤得到带有sr的tif
        .filter(t => t.contains("_sr_"))

      //.filter(t => !t.contains("aerosol"))
    val aerosol = files.filter(t => t.contains("aerosol"))

    val fs = files.filter(t => !t.contains("aerosol"))
    //对所有的.tif进行排序
    val pair = fs.map {
      f =>
        (f, f.charAt(f.length - 5).toInt)
    }

    aerosol ++ pair.sortBy(_._2).map(_._1)
  }

  def mkdir(dir: String): Unit = {
    val folder = new File(dir)
    if (!folder.exists() && !folder.isDirectory) {
      folder.mkdirs()
    }
  }

  def toColumn(str: Int):ColumnName = {
    new ColumnName(StringContext("band_","").s(str))
  }

  /**
    * 由于Kmeans非监督分类，预测后类的并不是按照原始数据值的大小来划分，而是根据聚类中心的排序。
    * */
  def centerToIndex (arr:Array[Double]): Map[Int, Int] ={
    /**
      * 原始的位置： 0, 1, 2, 3, 4, 5
      *
      * */
    /**得到每个cluster center对应的prediction值*/
    val res = arr.zipWithIndex.sortWith(_._1 < _._1)

    /**将每个prediction值改为按照cluster center的值从小到大排列
      * 这个map的结果，key为原始值，value为从下到大排列值。
      * */
    val result = res.map(_._2).zipWithIndex.toMap
    arr.foreach(x => print(x + ", "))
    println("-------------------------------------------------------------")
    result.foreach{case (k:Int, y:Int) => println("original: " + k + " => " + "result: "+ y  )}
    result
  }

}
