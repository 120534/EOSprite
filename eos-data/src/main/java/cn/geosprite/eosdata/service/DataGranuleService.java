package cn.geosprite.eosdata.service;

import cn.geosprite.eosdata.dao.SceneRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:44 2019-4-3
 * @ Description：None
 * @ Modified By：
 */

@Service
public class DataGranuleService {

    @Autowired
    private SceneRepository sceneRepository;

    @Transactional
    public void save(DataGranule dataGranule) {
        sceneRepository.save(dataGranule);
    }

}
