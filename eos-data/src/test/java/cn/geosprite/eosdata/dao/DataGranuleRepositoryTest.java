package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.dto.OrderInputDTO;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.enums.DataEnum;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    DataGranuleRepository dataGranuleRepository;


    @Test
    public void findByTile(){
        Pageable pageable = PageRequest.of(0,3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = Date.valueOf(LocalDate.of(2018,5,25));
        Date endDate = Date.valueOf(LocalDate.of(2019,2,25));
        Orders orders = ordersRepository.findByOrderId(1);
        Page<DataGranule> page1 = dataGranuleRepository.findByTile(
                orders.getDataProductName(),//L1TP_C1_T1
                orders.getDataSensorName(),//LC
                orders.getDataStartDate(),
                orders.getDataEndDate(),
                orders.getStartPath(),
                orders.getEndPath(),
                orders.getStartRow(),
                orders.getEndRow(),
                pageable
             );
    }

//    @Test
//    public void findByPathRow(){
//        Orders orders= ordersRepository.findByOrderId(12);
//
//        List<String> list = dataGranuleRepository.findDataGranuleProductIdByTile(
//                orders.getDataProductName(),
//                orders.getDataSensorName(),
//                orders.getDataStartDate(),
//                orders.getDataEndDate(),
//                orders.getStartPath(),
//                orders.getEndPath(),
//                orders.getStartRow(),
//                orders.getEndRow()
//        );
//        Assert.assertNotNull(list);
//
//    }

    @Test
    public void test(){
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO
                .setStartRow(24)
                .setEndRow(35)
                .setStartPath(113)
                .setEndPath(127)
                .setDataProductName("归一化植被指数(NDVI)")
                .setDataSensorName("Landsat-8")
                .setDate("01/01/2018 - 09/21/2018");
        Orders orders = orderInputDTO.converToOrders();
        Orders ordersReturn = ordersRepository.save(orders);

        System.out.println(ordersReturn.toString());
    }


}