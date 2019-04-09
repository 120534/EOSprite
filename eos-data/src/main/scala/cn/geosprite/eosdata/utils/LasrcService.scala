package cn.geosprite.eosdata.utils

import java.io.PrintWriter

object LasrcService {

    /**
     * description: 这个service用来进行大气校正，需要两个参数，输入和输出路径。在demo里面先自定义输出路径。
     * created time:  2019-3-28
     *
     *  params [inputPath, outputPath]
     * @return _root_.scala.Predef.String
     */

  def doLasrc(inputPath: String):String = {

    // inputPath "/mnt/disk1/geodata/lc8/dir/117/033/LC81170332018184LGN00"

    //这里的的输出路径已经定下来是由输入路径修改得来。
    // outputPath "/mnt/disk1/geodata/lc8/sr/117/033/LC81170332018184LGN00"
    val outputPath = inputPath.replace("dir","sr")

    import java.io.File

    def getName(inputPath:String): String ={
      val file = new File(inputPath)
      val files = file.listFiles().filter(! _.isDirectory)
        .filter(t => t.toString.endsWith("_MTL.txt"))
      val f = files.head.getName.split("_")
      f.take(f.length - 1).mkString("_")
    }

    val dataName = getName(inputPath)

    val cmd = "docker run " +
      s"-v $inputPath:/mnt/input-dir:ro " +
      s"-v $outputPath:/mnt/output-dir:rw -v /mnt/disk1/LaSRC:/mnt/lasrc-aux:ro " +
      "--rm -t geosprite-usgs-espa-lasrc " +
      dataName

    import scala.sys.process._
    val result = cmd.!!
    //需要考虑返回信息如何设置，以及异常处理

    //输出大气校正的日志信息
    def writeLog(str:String ): Unit ={
      val out = new PrintWriter(outputPath + "/" + dataName + ".log")
      out.write(str)
      out.close()
    }

    writeLog(result)
    result
  }
}
