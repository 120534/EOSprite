package cn.geosprite.eosdata.utils;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dataGranuleUtils.DataGranules;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.LandsatFormatCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:40 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UtilsTest {
    @Autowired
    private DataGranuleRepository dataGranuleRepository;


    @Test
    public void convertDataGranule() {
        DataGranule dataGranule = dataGranuleRepository.findDataGranuleByDataGranuleId("LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11");
        dataGranule = DataGranules.converter(dataGranule, LandsatFormatCode.DIR);
        dataGranuleRepository.save(dataGranule);
        DataGranule dataGranule1 = DataGranules.converter(dataGranule, LandsatFormatCode.SR);
        dataGranuleRepository.save(dataGranule1);
        dataGranule = DataGranules.converter(dataGranule1, LandsatFormatCode.NDVI_TIFF);
        dataGranuleRepository.save(dataGranule);
        dataGranule = DataGranules.converter(dataGranule1, LandsatFormatCode.NDVI_PNG);
        dataGranuleRepository.save(dataGranule);
    }

    @Test
    public void dataGranule() {
        DataGranule dataGranule = dataGranuleRepository.findDataGranuleByDataGranuleId("LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11");
        LandsatFormatCode fc = LandsatFormatCode.TGZ;

        log.warn("dataGranule converting warning src = {}, des = {}",dataGranule.getFormatCode(),fc.getFormat());
    }
}