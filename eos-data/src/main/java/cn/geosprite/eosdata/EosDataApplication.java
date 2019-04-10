package cn.geosprite.eosdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.geosprite")
public class EosDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(EosDataApplication.class, args);
    }

}
