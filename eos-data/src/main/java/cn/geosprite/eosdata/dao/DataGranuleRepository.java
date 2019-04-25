package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import monocle.std.string;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.Collection;
import java.util.List;


@Repository
public interface DataGranuleRepository extends JpaRepository<DataGranule, String>, JpaSpecificationExecutor<DataGranule> {

    DataGranule findDataGranuleByDataGranuleId(String dataGranuleId);

    /**
     * 使用OrderDataGranule的orderId查询得到其对应的List<DataGranule>
     * @param i
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT * from data_granules as a inner join order_data_granules as b on a.data_granule_id = b.data_granule_id where b.order_id = ?1 ;")
    List<DataGranule> findDataGranulesByOrderDataGranuleId(int i);

    @Query(value =
            "select u from DataGranule u where u.productCode = ?1 And u.sensorCode = ?2 " +
                    "and u.sceneDate between ?3 and ?4 " +
                    "and  substring(u.tileCode,1, 3) between ?5 and  ?6 " +
                    "and  substring(u.tileCode,5, 3) between ?7 and  ?8 " +
                    "order by u.sceneDate asc")
    Page<DataGranule> findByTile(String productCode,
                                 String sensorCode,
                                 Date startDate,
                                 Date endDate,
                                 String startPath,
                                 String endPath,
                                 String startRow,
                                 String endRow,
                                 Pageable pageable);

    /**
     * 根据查询条件返回符合的数据，通过分页进行展示
     * 查询条件包括
     * 1.sensor
     * 2.path, row
     * 3.date
     * 4.productCode
     */

    @Query(value =
            "select u from DataGranule u where u.productCode = ?1 And u.sensorCode = ?2 " +
                    "and u.sceneDate between ?3 and ?4 " +
                    "and  substring(u.tileCode,1, 3) between ?5 and  ?6 " +
                    "and  substring(u.tileCode,5, 3) between ?7 and  ?8 " +
                    "order by u.sceneDate asc")
    List<DataGranule> findByTile(String productCode,
                                 String sensorCode,
                                 Date startDate,
                                 Date endDate,
                                 String startPath,
                                 String endPath,
                                 String startRow,
                                 String endRow);

    @Query(value =
            "select u.dataGranuleId from DataGranule u where u.productCode = ?1 And u.sensorCode = ?2 " +
                    "and u.sceneDate between ?3 and ?4 " +
                    "and  substring(u.tileCode,1, 3) between ?5 and  ?6 " +
                    "and  substring(u.tileCode,5, 3) between ?7 and  ?8 " +
                    "order by u.sceneDate asc" )
    List<String> findDataGranuleIdByTile(String productCode,
                                               String sensorCode,
                                               Date startDate,
                                               Date endDate,
                                               String startPath,
                                               String endPath,
                                               String startRow,
                                               String endRow);
}


