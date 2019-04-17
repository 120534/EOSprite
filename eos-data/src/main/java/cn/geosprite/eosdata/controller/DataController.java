package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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


    private final DataServiceImpl dataService;

    @Autowired
    public DataController(DataServiceImpl dataService){
        this.dataService = dataService;
    }

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

        dataService.save(dataGranule);
        return dataGranule;
    }


    @RequestMapping(value = "/getAll", method=RequestMethod.GET)
    public String getEntryByPageable(Model model,
                                     @RequestParam(defaultValue = "1" ) Integer page,
                                     @RequestParam(defaultValue = "5" ) Integer size
                                                ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, new Sort(Sort.Direction.ASC, "sceneDate"));
        model.addAttribute("dataGranules", dataService.findAll(pageRequest));
        model.addAttribute("page", page);
        model.addAttribute("pageRequest", pageRequest);
        return "index";
    }
}
