package cn.geosprite.eosdata.service;

import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 19:15 2019-4-7
 * @ Description：操作数据库的全部放在dataservice中
 * @ Modified By：
 */
public interface DataService {

    //从orderId查找到对应的dataGranules
    List<DataGranule> findDataGranulesByOrderId(Integer i);

    //保存
    void save(DataGranule dataGranule);

    //分页查询全部
    Page<DataGranule> findAll(Pageable pageable);

}
