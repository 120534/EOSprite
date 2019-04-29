package cn.geosprite.eosdata.service;

import cn.geosprite.eosdata.entity.DataGranule;

import java.io.IOException;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 19:44 2019-4-7
 * @ Description：包含一些处理逻辑
 * @ Modified By：
 */
public interface ProcessService {

    List<DataGranule> doSR( List<DataGranule> dataGranules);

    List<DataGranule> doNDVI( List<DataGranule> dataGranules);

    List<DataGranule> doNDWI( List<DataGranule> dataGranules);
}
