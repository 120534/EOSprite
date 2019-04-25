package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:24 2019-4-22
 * @ Description：None
 * @ Modified By：
 */
public interface OrdersRepository extends JpaRepository<Orders,Integer> {

    Orders findByOrderId(Integer orderId);

}
