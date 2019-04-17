package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.List;


@Repository
public interface DataGranuleRepository extends JpaRepository<DataGranule, String> {

    DataGranule findDataGranuleByDataGranuleId(String dataGranuleId);

    List<DataGranule> findDataGranulesByDataGranuleId(List<String> dataGranuleIds);

    /**
     * 使用OrderDataGranule的orderId查询得到其对应的List<DataGranule>
     * @param i
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT * from data_granules as a inner join order_data_granules as b on a.data_granule_id = b.data_granule_id where b.order_id = ?1 ;")
    List<DataGranule> findDataGranulesByOrderDataGranuleId(int i);

    /**
     * 查找所有的数据，通过分页进行展示
     */
    List<DataGranule> findAllBySensorCode(String sensorCode, Pageable pageable);
}


