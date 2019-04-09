package cn.geosprite.eosprocess.service


import org.springframework.stereotype.Service


@Service
class LasrcService {

    /**
     * description: 这个service用来进行大气校正，需要两个参数，输入和输出路径。在demo里面先自定义输出路径。
     * created time:  2019-3-28
     *
     *  params [inputPath, outputPath]
     * @return _root_.scala.Predef.String
     */

  def doLasrc(inputPath: String):String = {

    val dataName = inputPath.split("/").last

    val outputPath = s"/home/hadoop/sr/$dataName"


    val cmd = "docker run " +
      s"-v $inputPath:/mnt/input-dir:ro " +
      s"-v $outputPath:/mnt/output-dir:rw -v /mnt/disk1/LaSRC:/mnt/lasrc-aux:ro " +
      "--rm -t geosprite-usgs-espa-lasrc " +
      dataName

    import scala.sys.process._
    val result = cmd.!!
    //需要考虑返回信息如何设置，以及异常处理
    result
  }
}
