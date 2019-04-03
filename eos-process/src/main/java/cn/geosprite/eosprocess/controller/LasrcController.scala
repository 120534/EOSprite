package cn.geosprite.eosprocess.controller

import cn.geosprite.eosprocess.service.LasrcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.ModelAndView

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 10:34 2019-3-28
  * @ Description：None
  * @ Modified By：
  */
@Controller
class LasrcController {

  @Autowired
  private var lasrc:LasrcService = _

  @GetMapping(Array("/lasrc"))
  @ResponseBody
  def doLasrc(@RequestParam path:String):String={
//    for example, the path is /home/hadoop/LC08_L1TP_121039_20180325_20180404_01_T1
    lasrc.doLasrc(path)
  }
}
