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

    //返回值还需要考虑如何设置，返回成功与否的信息，以及处理后的数据信息
    List<DataGranule> doLasrc(List<DataGranule> dirList);

    //unzip data

    List<DataGranule> unzip(List<DataGranule> tgzList);

    List<DataGranule> doNdvi(List<DataGranule> srList);

    DataGranule downloadData(DataGranule data);



}
