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

    private DataServiceImpl dataService;
    private ProcessServiceImpl processService;

    @Autowired
    public ProcessController(DataServiceImpl dataService, ProcessServiceImpl processService){
        this.dataService = dataService;
        this.processService = processService;
    }

    /**
     * 测试数据解压缩，以及数据入库。 测试ok
     */
    @GetMapping("/unzip")
    public DataGranule unzip(@RequestParam Integer orderId){
      List<DataGranule> dataGranuleList  = dataService.findDataGranulesByOrderId(orderId);
      List<DataGranule> unzippedDataGranule = processService.unzip(dataGranuleList);

      return unzippedDataGranule.get(0);
    }

    @GetMapping("/lasrc")
    public DataGranule lasrc(@RequestParam Integer orderId){
        List<DataGranule> dataGranuleList  = dataService.findDataGranulesByOrderId(orderId);
        List<DataGranule> unzippedDataGranule = processService.doSR(dataGranuleList);

        return unzippedDataGranule.get(0);
    }

    @GetMapping("/ndvi")
    public DataGranule ndvi(@RequestParam Integer orderId){
        List<DataGranule> dataGranuleList  = dataService.findDataGranulesByOrderId(orderId);
        List<DataGranule> unzippedDataGranule = processService.doNDVI(dataGranuleList);

        return unzippedDataGranule.get(0);
    }
}
