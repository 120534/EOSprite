package cn.geosprite.eosdata.service;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dao.OrderDataGranuleRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:44 2019-4-3
 * @ Description：None
 * @ Modified By：
 */

@Service
public class OrderDataGranuleService {

    @Autowired
    private OrderDataGranuleRepository orderGranuleRepository;

    @Transactional
    public void save(OrderDataGranule orderDataGranule) {

        orderGranuleRepository.save(orderDataGranule);
    }

}
