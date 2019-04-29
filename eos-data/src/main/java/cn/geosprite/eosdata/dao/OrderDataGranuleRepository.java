package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.OrderDataGranule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:42 2019-4-3
 * @ Description：None
 * @ Modified By：
 */
@Repository
public interface OrderDataGranuleRepository extends JpaRepository<OrderDataGranule, Integer> {

    Page<OrderDataGranule> findOrderDataGranulesByOrderId(Integer orderId, Pageable pageable);

    List<OrderDataGranule> findOrderDataGranulesByOrderId(Integer orderId);

}
