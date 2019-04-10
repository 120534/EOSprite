package cn.geosprite.eosdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.geosprite.eosprocess","cn.geosprite.eosdata"})
public class EosDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(EosDataApplication.class, args);
    }

}
