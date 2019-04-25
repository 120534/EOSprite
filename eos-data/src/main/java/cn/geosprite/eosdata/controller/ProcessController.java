package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import cn.geosprite.eosdata.service.impl.ProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 20:30 2019-4-8
 * @ Description：None
 * @ Modified By：
 */

@RestController
public class ProcessController {

    private ProcessServiceImpl processService;

    @Autowired
    public ProcessController(ProcessServiceImpl processService) {
        this.processService = processService;
    }

    @GetMapping("/lasrc")
    public DataGranule lasrc(@RequestParam Integer orderId){
        List<DataGranule> unzippedDataGranule = processService.doSR(orderId);

        return unzippedDataGranule.get(0);
    }

    @GetMapping("/ndvi")
    public DataGranule ndvi(@RequestParam Integer orderId){
        List<DataGranule> unzippedDataGranule = processService.doNDVI(orderId);

        return unzippedDataGranule.get(0);
    }

    @GetMapping("/ndwi")
    public DataGranule ndwi(@RequestParam Integer orderId){
        List<DataGranule> unzippedDataGranule = processService.doNWVI(orderId);

        return unzippedDataGranule.get(0);
    }


}
