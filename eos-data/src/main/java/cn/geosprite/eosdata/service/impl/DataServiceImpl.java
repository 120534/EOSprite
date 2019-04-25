package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dao.OrderDataGranuleRepository;
import cn.geosprite.eosdata.dao.OrdersRepository;
import cn.geosprite.eosdata.dto.DataGranuleOutputDTO;
import cn.geosprite.eosdata.dto.OrderInputDTO;
import cn.geosprite.eosdata.dto.OrderOutputDTO;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:44 2019-4-3
 * @ Description：数据的管理
 * @ Modified By：
 */

@Service
public class DataServiceImpl implements DataService {
    private final DataGranuleRepository dataGranuleRepository;
    private final OrderDataGranuleRepository orderDataGranuleRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    public DataServiceImpl(DataGranuleRepository dataGranuleRepository,
                           OrderDataGranuleRepository orderDataGranuleRepository,
                           OrdersRepository ordersRepository) {
        this.dataGranuleRepository = dataGranuleRepository;
        this.orderDataGranuleRepository = orderDataGranuleRepository;
        this.ordersRepository = ordersRepository;
    }

    public List<DataGranule> findDataGranulesByOrderId(Integer i){
        return dataGranuleRepository.findDataGranulesByOrderDataGranuleId(i);
    }

    @Override
    public void save(DataGranule dataGranule) {
        dataGranuleRepository.save(dataGranule); }

    /**
     * 分页测试
     * @param pageable
     * @return
     */
    @Override
    public Page<DataGranule> findAll(Pageable pageable) {
        Sort sort = new Sort(Sort.Direction.ASC,"sceneDate");
        PageRequest pr =  PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);
        return dataGranuleRepository.findAll(pr);
    }

    /**
     * 分页查询符合订单信息的dataGranules, 同时还要存储数据到orderDataGranule
     * @param orderInputDTO
     * @param pageable
     * @return
     */
    //TODO :预留方法，后期不需要刻意删除。
    public Page<OrderDataGranule> findByOrder(OrderInputDTO orderInputDTO, Pageable pageable){
        Orders orders = orderInputDTO.converToOrders();
        //存储订单信息
        Orders orders1 = ordersRepository.save(orders);
        /**
         *  将dataGranuleId信息写到OrderDataGranule里面，
         *  由于暂时数据源只从本地调用，那么直接使用dataGranule里面存储的数据信息。
         */
        //返回分页dataGranuleId信息。
        Integer id = orders.getOrderId();
        /**
         * 根据order信息，查询符合条件的dataGranule信息，然后存储到order_data_granule中
         */
        List<OrderDataGranule> orderDataGranules = findDataGranuleIdByTile(orders1);
        orderDataGranuleRepository.saveAll(orderDataGranules);
        findDataGranuleIdByTile(orders1);
        //重新以分页order信息返回
        return orderDataGranuleRepository.findOrderDataGranulesByOrderId(id, pageable);
    }
        /**
         * 根据order信息，查询符合条件的dataGranule信息，然后存储到order_data_granule中并返回
         */
    private  List<OrderDataGranule> findDataGranuleIdByTile(Orders orders){
        Integer id = orders.getOrderId();
        List<DataGranule> dataGranules = dataGranuleRepository.findByTile(
                orders.getDataProductName(),
                orders.getDataSensorName(),
                orders.getDataStartDate(),
                orders.getDataEndDate(),
                orders.getStartPath(),
                orders.getEndPath(),
                orders.getStartRow(),
                orders.getEndRow()
        );
        return dataGranules.stream().map(x -> new OrderDataGranule().setOrderId(id).setDataGranule(x)).collect(Collectors.toList());
    }

    /**
     * order返回orderOuputDTO对象,同时查询符合条件的dataGranule信息，写到order_data_granule表中。
     */
    public OrderOutputDTO orderReply(OrderInputDTO orderInputDTO){
        Orders orders = orderInputDTO.converToOrders();
        //存储订单信息
        Orders orders1 = ordersRepository.save(orders);
        List<OrderDataGranule> orderDataGranules = findDataGranuleIdByTile(orders1);
        orderDataGranuleRepository.saveAll(orderDataGranules);
        return orderInputDTO.convertToOrderOutputDTO(orders1);
    }

    /**
     * 根据orderId,查询得到orderDataGranule
     * 将orderDataGranule，转换为OrderDataGranule
     * 最后返回page<DataGranuleOutputDTO> 给modal界面
     */
    public Page<DataGranuleOutputDTO> finddataGranuleOutputDTOByOrderId(Integer orderId, Pageable pageable){
        Page<OrderDataGranule> page = orderDataGranuleRepository.findOrderDataGranulesByOrderId(orderId,pageable);
        return page.map(x -> DataGranuleOutputDTO.converToDataGranuleOutputDTO(x.getDataGranule()));
    }


    /**
     * 确认订单状态，完成或者未完成。
     */
    public Boolean checkOrderStatus(Integer id){
        Orders orders = ordersRepository.findByOrderId(id);
        return orders.getOrderCompletedTime()!= null;
    }

    
}
