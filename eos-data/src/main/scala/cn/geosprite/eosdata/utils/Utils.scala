package cn.geosprite.eosdata.utils

import java.io.{File, IOException}

import cn.geosprite.eosdata.entity.DataGranule
import cn.geosprite.eosdata.enums.FormatCode
import org.rauschig.jarchivelib.{ArchiveFormat, ArchiverFactory, CompressionType}

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 10:56 2019-4-8
  * @ Description：从压缩文件中获取数据，改写成解压缩后文件信息。 raw dataGranule to unzipped dataGranule
  * @ Modified By：
  */

object Utils {

  /**
    * 分割DataGranule的path字段
    * 如："raw/117/050/LC81170502019011LGN00.tgz"  ->  dir/117/050/LC81170502019011LGN00
    */
  def pathUpdate (string: String,format: String): String ={
    val strArray = string.split("/")
    strArray.update(0,format)
    strArray.update(strArray.length-1,strArray.last.substring(0,strArray.last.length-4))
    strArray.mkString("/")
  }
  
  /**
   * description: LC08/L1TP_C1_T1/117050/2019-01-11 -> LC08/L1TP_C1_T1_dir/117050/2018-09-21
   * created time:  2019-4-8
   * 
   *  params 
   * @return 
   */
  def idUpdate(string: String, format: String): String = {
    val seq = string.split("/")
    seq.update(1,seq(1).concat("_" + format.toUpperCase))
    seq.mkString("/")
  }

  /**
    * 将不同类型的dataGranule进行转换，
    * 需要重新设置四个信息，分别是data_granule_id，product_code，format_code，data_granule_path
    * 例如本地存储的tgz格式的dataGranule，在进行解压缩后需要将解压后的数据信息写入到数据库。
    * 示例： tgz 类型dataGranule_tgz 转换为dir
    *        convertDataGranule()
    *
    * @param dataGranule
    * @param targetType
    * @return
    */
  def convertDataGranule(dataGranule: DataGranule, targetType: String): DataGranule ={

    val id = dataGranule.getDataGranuleId

    val path = dataGranule.getDataGranulePath

    //需要用到原来dataGranule的信息
    val enum = FormatCode.fromTypeName(targetType)

    val fc  = enum.getFormat
    val dgi:String = idUpdate(id, enum.getFormat)
    val uri:String = dataGranule.getDataGranuleUri
    val ds = dataGranule.getDataSource
    val pc = dataGranule.getProductCode
    val sd = dataGranule.getSceneDate
    val sc = dataGranule.getSensorCode
    val tc = dataGranule.getTileCode
    val dp = pathUpdate(path, enum.getFormat)

    val converted = new DataGranule(dgi,sc,pc,tc,sd,fc,ds,dp,uri)
    converted
  }

   def unzip(input: String, output: String): Unit = {
      val destination = new File(output)
      val source = new File(input)
      val archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
      try
        archiver.extract(source, destination)
      catch {
        case e: IOException =>
          e.printStackTrace()
        case e: Exception =>
          e.printStackTrace()
      }
  }



}
