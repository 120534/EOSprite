package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dao.OrderDataGranuleRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:44 2019-4-3
 * @ Description：数据的管理
 * @ Modified By：
 */

@Service
public class DataServiceImpl implements DataService {
    private final DataGranuleRepository dataGranuleRepository;
    private final OrderDataGranuleRepository orderDataGranuleRepository;

    @Autowired
    public DataServiceImpl(DataGranuleRepository dataGranuleRepository,OrderDataGranuleRepository orderDataGranuleRepository){
        this.dataGranuleRepository = dataGranuleRepository;
        this.orderDataGranuleRepository = orderDataGranuleRepository;
    }

    public List<DataGranule> findDataGranulesByOrderId(Integer i){
        return dataGranuleRepository.findDataGranulesByOrderDataGranuleId(i);
    }

    @Override
    public void save(DataGranule dataGranule) {
        dataGranuleRepository.save(dataGranule); }

    @Override
    public Page<DataGranule> findAll(Pageable pageable) {
            return dataGranuleRepository.findAll(pageable);
    }

    public List<DataGranule> ndviPredicate(List<DataGranule> list){
//        list.stream().filter(x -> x.getDataGranuleId());

        return list;
    }

}
