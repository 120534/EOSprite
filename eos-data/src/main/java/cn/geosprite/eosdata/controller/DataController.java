package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.dto.DataGranuleOutputDTO;
import cn.geosprite.eosdata.dto.OrderInputDTO;
import cn.geosprite.eosdata.dto.OrderOutputDTO;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import org.geolatte.geom.M;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/getAll", method=RequestMethod.GET)
    public String getEntryByPageable(Model model,
                                     @RequestParam(defaultValue = "1" ) Integer page,
                                     @RequestParam(defaultValue = "5" ) Integer size
                                                ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, new Sort(Sort.Direction.ASC, "sceneDate"));
        model.addAttribute("dataGranules", dataService.findAll(pageRequest));
        model.addAttribute("page", page);
        model.addAttribute("pageRequest", pageRequest);
        return "order";
    }

    /**
     * 接收前端form，得到OrderInputDTO，转化为Orders存储到本地数据库
     * 再进行查询，符合order要求的dataGranule
     * 测试数据
     * LC08/L1TP_C1_T1_NDVI/TIFF/117/043/2018/03/29	LC08	L1TP_C1_T1_NDVI	117/043	2018-03-29	TIFF	USGS	LC08/L1TP_C1_T1_NDVI/TIFF/117/043/2018/03/29/LC81170432018088LGN00.TIFF
     * @param oderInputDTO
     * @return
     */
    @RequestMapping(value = "", method=RequestMethod.POST)
    @ResponseBody
    public Page<OrderDataGranule> postOrder(OrderInputDTO oderInputDTO,
                                            @RequestParam(defaultValue = "1" ) Integer page,
                                            @RequestParam(defaultValue = "5" ) Integer size)
    {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<OrderDataGranule> pages = dataService.findByOrder(oderInputDTO, pageRequest);
        return dataService.findByOrder(oderInputDTO, pageRequest);
    }

    /**
     * index界面提交order订单后，返回orders信息到前台。
     */
    @RequestMapping(value = "/postOrder", method=RequestMethod.POST)
    public ModelAndView saveOrderAndReturnOrder(OrderInputDTO oderInputDTO)
    {
        ModelAndView  modelAndView = new ModelAndView("orders.html");
        OrderOutputDTO orderOutputDTO = dataService.orderReply(oderInputDTO);
        modelAndView.addObject("orderOutputDTO",orderOutputDTO);
        return modelAndView;
    }


    @RequestMapping(value = "/postOrder/orderDetail", method=RequestMethod.GET)
    @ResponseBody
    public Page<DataGranuleOutputDTO> orderDetail(@RequestParam Integer orderId,
                                            @RequestParam(defaultValue = "1" ) Integer page,
                                            @RequestParam(defaultValue = "5" ) Integer size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<DataGranuleOutputDTO> dataGranuleOutputDTOs =
                dataService.finddataGranuleOutputDTOByOrderId(orderId, pageRequest);

        return dataGranuleOutputDTOs;
    }

}
