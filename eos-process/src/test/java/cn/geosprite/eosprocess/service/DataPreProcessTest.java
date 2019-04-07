package cn.geosprite.eosprocess.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 11:05 2019-4-7
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataPreProcessTest {

    @Autowired DataPreProcess dataPreProcess;

    @Test
    public void unzip() {
        try {
            dataPreProcess.unzip("D:\\GLDAS_test\\LC81980302018031LGN00.tgz","D:\\GLDAS_test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}