package cn.geosprite.eosprocess.controller

import java.io.{File, IOException}
import java.nio.file.{Files, Paths}

import cn.geosprite.eosprocess.service.BandMathService
import cn.geosprite.eosprocess.utils.Utils.outPath
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileUrlResource
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation._

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 15:34 2019-3-22
  * @ Description：None
  * @ Modified By：
  */
@RestController
class ProcessController {

  @Autowired
  private var service: BandMathService = _

  //controller for ndvi caculating
  @RequestMapping(method = Array(RequestMethod.POST), path = Array("/ndvi"))
  def getNdvi(@RequestParam name: String)= {
    //for example, the name is "L8-Elkton-VA"
    service.getNdvi(name)
  }

  //controller for ndvi .png image generating
  @RequestMapping(value = Array("/png"), method = Array(RequestMethod.GET), produces = Array(MediaType.IMAGE_PNG_VALUE))
  @throws[IOException]
  def getNdviImage(@RequestParam name: String, response: HttpServletResponse): Unit = {
    if (!Files.exists(Paths.get(outPath(name, "png")))) service.getNdvi(name)

    response.setContentType(MediaType.IMAGE_PNG_VALUE)
    val imgFile = new FileUrlResource(outPath(name, "png"))
    //return image to frontend
    StreamUtils.copy(imgFile.getInputStream, response.getOutputStream)

  }
}
