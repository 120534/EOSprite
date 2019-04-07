package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shapeless.ops.hlist;

import java.util.Collection;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:42 2019-4-3
 * @ Description：None
 * @ Modified By：
 */
@Repository
public interface OrderDataGranuleRepository extends CrudRepository<OrderDataGranule, Integer> {

    List<OrderDataGranule> findOrderDataGranulesByOrderId(Integer i);


}
