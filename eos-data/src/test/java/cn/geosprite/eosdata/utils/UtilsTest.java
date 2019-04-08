package cn.geosprite.eosdata.utils;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:40 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {

    @Autowired
    DataServiceImpl dataService ;
    @Test
    public void convertDataGranule() {
        List<DataGranule> list = dataService.findDataGranulesByOrderId(1);

        for (DataGranule ds : list){
        DataGranule converted = Utils.convertDataGranule(ds,"dir");
        dataService.save(converted);
        }
    }
}