package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.DataGranuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:48 2019-4-3
 * @ Description：None
 * @ Modified By：
 */
@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DataGranuleService dataGranuleService;

    @RequestMapping("/save")
    @ResponseBody
    public DataGranule save(){
        // data for
        DataGranule dataGranule = new DataGranule();
        dataGranule.setDataGranuleId("LC08/L1TP_C1_T1/117050/2020-01-01");
        dataGranule.setSensorCode("LC08");
        Date date = new Date(System.currentTimeMillis());
        dataGranule.setSceneDate(date);
        dataGranule.setFormatCode("tgz");
        dataGranule.setTileCode("111111");
        dataGranule.setDataSource("USGS");
        dataGranule.setProductCode("L1TP_C1_T1");
        dataGranule.setDataGranulePath("117/050/LC81170502019027LGN00.tgz");
        dataGranule.setDataGranuleUri(null);

        dataGranuleService.save(dataGranule);
        return dataGranule;
    }
}
