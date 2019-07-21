package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.entity.DataGranule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:55 2019-4-9
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessServiceImplTest {

    @Autowired
    ProcessServiceImpl processService;

    @Autowired
    DataServiceImpl dataService;

    @Test
    public void unzip() {
        List<DataGranule> list = dataService.findDataGranulesByOrderId(3);

    }
}