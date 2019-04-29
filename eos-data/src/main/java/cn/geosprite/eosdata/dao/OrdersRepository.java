package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:24 2019-4-22
 * @ Description：None
 * @ Modified By：
 */
public interface OrdersRepository extends JpaRepository<Orders,Integer> {

    Orders findByOrderId(Integer orderId);

    /**订单完成时，修改订单的完成时间字段*/

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update DataGranule u set u.dataGranuleUri = ?2, u.dataGranulePreview = ?3 where u.dataGranuleId = ?1 ")
    void updateDataGranuleURIAndPreview(String id, String uri,String preview);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update Orders o set o.orderCompletedTime = ?2, o.status = ?3 where o.orderId = ?1")
    void updateOrderFinish(Integer orderId, Timestamp completeTime, Integer orderStatus);

}
