package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.service.ProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:15 2019-4-7
 * @ Description：None
 * @ Modified By：
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProcessServiceImplTest {

    @Autowired
    ProcessService processService;

    @Test
    public void doLasrc() {
        processService.doLasrc(1);
    }
}