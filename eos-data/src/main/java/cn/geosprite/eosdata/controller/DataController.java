package cn.geosprite.eosdata.controller;

import cn.geosprite.eosdata.dto.DataGranuleOutputDTO;
import cn.geosprite.eosdata.dto.OrderInputDTO;
import cn.geosprite.eosdata.dto.OrderOutputDTO;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
     * index界面提交order订单后，返回orders信息到前，等前台确认下单。
     */
    @RequestMapping(value = "/postOrder", method=RequestMethod.POST)
    public ModelAndView saveOrderFormReturnOrder(OrderInputDTO oderInputDTO)
    {
        ModelAndView  modelAndView = new ModelAndView("orders.html");
        /**返回订单信息给用户*/
        OrderOutputDTO orderOutputDTO = dataService.orderInfoReply(oderInputDTO);
        modelAndView.addObject("orderOutputDTO",orderOutputDTO);

        /** 返回modal中的订单详细信息给用户 */
        List<DataGranuleOutputDTO> dataGranuleOutputDTOs =
                dataService.orderDetailInfoReply(orderOutputDTO);
        modelAndView.addObject("dataGranuleOutputDTOs", dataGranuleOutputDTOs);
        return modelAndView;
    }
}
