package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:42 2019-4-3
 * @ Description：None
 * @ Modified By：
 */
@Repository
public interface DataGranuleRepository extends CrudRepository<DataGranule, String> {

}
