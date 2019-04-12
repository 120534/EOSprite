package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 12:27 2019-4-9
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataGranuleRepositoryTest {
    @Autowired
    OrderDataGranuleRepository orderDataGranuleRepository;

    @Test
    public void findDataGranulesByDataGranuleId() {
        List<OrderDataGranule> list = orderDataGranuleRepository.findOrderDataGranulesByOrderId(1);
        list.forEach(x -> System.out.println(x.toString()));
//
//        OrderDataGranule d = new OrderDataGranule();
//        DataGranule g = new DataGranule();
//        g.setDataGranuleId("LC08/L1TP_C1_T1/117050/2019-09-21");
//        g.setSensorCode("sensorCode");
//        g.setProductCode("proCode");
//        g.setTileCode("tileCode");
//        g.setSceneDate(new java.sql.Date(System.currentTimeMillis()));
//        g.setFormatCode("forCode");
//        g.setDataSource("source");
//        d.setOrderId(1);
//        d.setDataGranule(g);
//
//        orderDataGranuleRepository.save(d);
    }
}