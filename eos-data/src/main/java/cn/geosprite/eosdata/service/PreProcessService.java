package cn.geosprite.eosdata.service;

import cn.geosprite.eosdata.entity.DataGranule;

import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 19:44 2019-4-7
 * @ Description：包含一些处理逻辑
 * @ Modified By：
 */
public interface PreProcessService {

    DataGranule downloadData(DataGranule data);

    List<DataGranule> extractFiles(List<DataGranule> tgzList);
}
