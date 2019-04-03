package cn.geosprite.eosprocess.utils

import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}

import com.typesafe.config.ConfigFactory
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.render.ColorMap
import javax.imageio.ImageIO

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 9:31 2019-3-25
  * @ Description：None
  * @ Modified By：
  */
object Utils {

  def readTiff(name: String): SinglebandGeoTiff = SinglebandGeoTiff(s"$name" + ".tiff")

  val ndvi_colorMap:ColorMap = ColorMap.fromStringDouble(ConfigFactory.load().getString("colorMap.ndvi")).get
  val ndwi_colorMap:ColorMap = ColorMap.fromStringDouble(ConfigFactory.load().getString("colorMap.ndwi")).get

  //设置输出路径
  def outPath(name: String, t: String):String = "src/main/resources/image/" + name + "." + t

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
}
