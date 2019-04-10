package cn.geosprite.eosprocess

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
  * @ Author     ：wanghl
  * @ Date       ：Created in 9:45 2019-4-10
  * @ Description：None
  * @ Modified By：
  */

@SpringBootApplication
class EosProcessApplication {
}
object Main {
  def main(args: Array[String]): Unit = {

    SpringApplication.run(classOf[EosProcessApplication])

  }
}
