package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.dto.OrderStatus;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import cn.geosprite.eosdata.service.impl.ProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 20:30 2019-4-8
 * @ Description：None
 * @ Modified By：
 */

@RestController
public class ProcessController {

    private ProcessServiceImpl processService;
    private DataServiceImpl dataService;

    @Autowired
    public ProcessController(ProcessServiceImpl processService) {
        this.processService = processService;
    }

    @GetMapping("/process")
    @ResponseBody
    public OrderStatus process(@RequestParam Integer orderId){
        /**
         * 前端发送orderId信息,处理数据，返回处理后的订单状态
         */
     return processService.process(orderId);

    }


    @GetMapping("/test")
    @ResponseBody
    private OrderStatus test(@RequestParam Integer orderId){

        return  new OrderStatus()
                .setMessage("处理完成")
                .setOrderCompletedTime(new Timestamp(System.currentTimeMillis()));

    }
}
