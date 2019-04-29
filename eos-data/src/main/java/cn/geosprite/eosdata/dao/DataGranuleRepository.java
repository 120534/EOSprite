package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * @ Date       ：Created in 21:14 2019-4-7
 * @ Description：None
 * @ Modified By：
 * @author wanghl
 */

@SuppressWarnings("AlibabaTransactionMustHaveRollback")
@Repository
public interface DataGranuleRepository extends JpaRepository<DataGranule, String>{

    /**
     * 更新dataGranuleURI,以及preview
     * @param id
     * @param uri
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update DataGranule u set u.dataGranuleUri = ?2, u.dataGranulePreview = ?3 where u.dataGranuleId = ?1 ")
    void updateDataGranuleURIAndPreview(String id, String uri,String preview);

    /**
     * 根据id查询DataGranule
     * @param dataGranuleId
     * @return
     */
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


    /** **查询符合条件的数据产品Id**
     * productCode 由于这里查询的是原始数据，肯定不能按照数据产品查询。
     * 1.sensor
     * 2.path, row
     * 3.date
     * */
    @Query(value =
            "select u from DataGranule u where u.productCode = ?1 And u.sensorCode = ?2 " +
                    "and u.sceneDate between ?3 and ?4 " +
                    "and  substring(u.tileCode,1, 3) between ?5 and  ?6 " +
                    "and  substring(u.tileCode,5, 3) between ?7 and  ?8 " +
                    "and u.formatCode = ?9 " +
                    "order by u.sceneDate asc" )
    List<DataGranule> findDataGranuleProductIdByTile(String productCode,
                                                String sensorCode,
                                                Date startDate,
                                                Date endDate,
                                                String startPath,
                                                String endPath,
                                                String startRow,
                                                String endRow,
                                                     String formatCode);

    /** **查询符合条件的数据产品Id**
     * productCode 由于这里查询的是原始数据，肯定不能按照数据产品查询。
     * 1.sensor
     * 2.path, row
     * 3.date
     * */
    @Query(value =
            "select u from DataGranule u where " +
                    "u.sensorCode = ?1 " +
                    "and u.sceneDate between ?2 and ?3 " +
                    "and  substring(u.tileCode,1, 3) between ?4 and  ?5 " +
                    "and  substring(u.tileCode,5, 3) between ?6 and  ?7 ")
    List<DataGranule> findDataGranuleRawIdByTile(String sensorCode,
                                            Date startDate,
                                            Date endDate,
                                            String startPath,
                                            String endPath,
                                            String startRow,
                                            String endRow);
}


