package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dao.OrderDataGranuleRepository;
import cn.geosprite.eosdata.dao.OrdersRepository;
import cn.geosprite.eosdata.dataGranuleUtils.DataGranules;
import cn.geosprite.eosdata.dto.DataGranuleOutputDTO;
import cn.geosprite.eosdata.dto.OrderInputDTO;
import cn.geosprite.eosdata.dto.OrderOutputDTO;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.OrderDataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.enums.LandsatFormatCode;
import cn.geosprite.eosdata.service.DataService;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
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

    @Override
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
    //预留方法，后期不需要可以删除。
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
        List<OrderDataGranule> orderDataGranules1 = orderDataGranuleRepository.saveAll(orderDataGranules);

        //重新以分页order信息返回
        return orderDataGranuleRepository.findOrderDataGranulesByOrderId(id, pageable);
    }
        /**
         * 根据order信息，查询符合条件的dataGranule信息，然后存储到order_data_granule中并返回
         */

    private  List<OrderDataGranule> findDataGranuleIdByTile(Orders orders){
        Integer id = orders.getOrderId();
        List<DataGranule> dataGranules = dataGranuleRepository.findDataGranuleProductIdByTile(
                orders.getDataProductName(),
                orders.getDataSensorName(),
                orders.getDataStartDate(),
                orders.getDataEndDate(),
                orders.getStartPath(),
                orders.getEndPath(),
                orders.getStartRow(),
                orders.getEndRow(),
                "TIFF"
        );
        return dataGranules.stream().map(x -> new OrderDataGranule().setOrderId(id).setDataGranule(x)).collect(Collectors.toList());
    }

    /**
     * 前端提交order form
     * 返回orderOuputDTO对象,
     * 同时查询符合条件的dataGranule信息，
     * 写到order_data_granule表中，
     * 处理dataGranule数据
     */
    public OrderOutputDTO orderInfoReply(OrderInputDTO orderInputDTO){
        Orders orders = orderInputDTO.converToOrders();
        //存储订单信息
        Orders orders1 = ordersRepository.save(orders);
        return orderInputDTO.convertToOrderOutputDTO(orders1);
    }

    /**
     * 返回订单的详细信息给modal
     * 也就是OrderDataGranule信息
     */
    public List<DataGranuleOutputDTO> orderDetailInfoReply(OrderOutputDTO orderOutputDTO){

        Integer orderId = orderOutputDTO.getOrderId();
        Orders orders1 = ordersRepository.findByOrderId(orderId);
        //如 L1TP_C1_T1_NDVI
        String productName = orders1.getDataProductName();
        /**
         * 查询完全满足条件的DataGranule信息，写入OrderDataGranule表中,
         */
        List<DataGranule> productDataGranule = findProductDataByOrder(orders1);
        /**
         * 对没有符合条件的数据产品dataGranule进行提前生成数据产品信息入库,要去除那部分已经生成的product，
         * 确保OrderDataGranule表已经写入信息，可以返回给前端。
         */
        List<DataGranule> rawDataGranule = findRawDataByOrder(orders1);

        /**使用两给List是为了后面处理，对converted进行remove，预先把数据结果放入datagranule表中，
         * 防止部分数据已经存储，对其进行更新。
         * result可以直接返回DataGranule给前端。
         * */
        List<DataGranule> converted = new ArrayList<>();
        /**应该返回的dataGranule产品信息*/
        for (DataGranule dataGranule :rawDataGranule){
            converted.add(DataGranules.converterForward(dataGranule, LandsatFormatCode.fromProductCode(productName)));
        }
        /**把新的dataGranule产品信息入库，需要先出去掉已经有的产品信息，防止重复插入数据*/
        if (productDataGranule.size() != 0){
            //不能直接移除，product里面的dataGranule于converted里面的不一样,去除ID信息
            List<String> dataGranule2 = productDataGranule.stream().map(DataGranule::getDataGranuleId).collect(Collectors.toList());
            /**如果id被包含在product里面，那么过滤掉*/
            converted = converted.stream().filter(x -> !dataGranule2.contains(x.getDataGranuleId())).collect(Collectors.toList());
        }
        /**查询orderDataGranule，写入到OrderDataGranule表中*/
        dataGranuleRepository.saveAll(converted);

        List<DataGranule> result = findProductDataByOrder(orders1);
        List<OrderDataGranule> orderDataGranules = result
                .stream()
                .map(x -> new OrderDataGranule().setOrderId(orderId).setDataGranule(x)).collect(Collectors.toList());

        List<OrderDataGranule> dataGranuleOutputDTOs = orderDataGranuleRepository.saveAll(orderDataGranules);
        return dataGranuleOutputDTOs.stream()
                .map(x -> DataGranuleOutputDTO.converToDataGranuleOutputDTO(x.getDataGranule()))
                .collect(Collectors.toList());
    }

    /**
     * 根据orderId,查询得到orderDataGranule
     * 将orderDataGranule，转换为OrderDataGranule
     * 最后返回page<DataGranuleOutputDTO> 给modal界面,分页显示
     */
    public Page<DataGranuleOutputDTO> findDataGranuleOutputDTOByOrderId(Integer orderId, Pageable pageable){
        Page<OrderDataGranule> page = orderDataGranuleRepository.findOrderDataGranulesByOrderId(orderId,pageable);
        return page.map(x -> DataGranuleOutputDTO.converToDataGranuleOutputDTO(x.getDataGranule()));
    }

    /**
     * 根据orderId,查询得到orderDataGranule
     * 将orderDataGranule，转换为OrderDataGranule
     * 最后返回page<DataGranuleOutputDTO> 给modal界面，滑动显示
     */
    public List<DataGranuleOutputDTO> findDataGranuleOutputDTOByOrderId(Integer orderId){
       List<OrderDataGranule> list =  orderDataGranuleRepository.findOrderDataGranulesByOrderId(orderId);
       return list.stream().map(x -> DataGranuleOutputDTO.converToDataGranuleOutputDTO(x.getDataGranule())).collect(Collectors.toList());
    }


    /**
     * 确认订单状态，完成或者未完成。
     */
    public Boolean checkOrderStatus(Integer id){
        Orders orders = ordersRepository.findByOrderId(id);
        return orders.getOrderCompletedTime()!= null;
    }

    /**
     * 根据order 返回对应订单对应的所有原始dataGranule信息
     * @param orders
     * @return
     */
    public List<DataGranule> findRawDataByOrder(Orders orders){

        String sensorName = orders.getDataSensorName();
        String landsatSensorName = "LC08";
            if (landsatSensorName.equalsIgnoreCase(sensorName)) {
            /** 原始数据 */
            return dataGranuleRepository.findDataGranuleProductIdByTile(
                    "L1TP_C1_T1",
                    orders.getDataSensorName(),
                    orders.getDataStartDate(),
                    orders.getDataEndDate(),
                    orders.getStartPath(),
                    orders.getEndPath(),
                    orders.getStartRow(),
                    orders.getEndRow(),
                    "TGZ"
            );
        }else {
            //TODO:add methods for other dataSets
                throw  new AssertionError("no methods for other dataSets except landsat8");
        }
    }

    /** 根据order条件，查询已经处理过的完全符合条件的数据产品*/
    public List<DataGranule> findProductDataByOrder(Orders orders) {
        String sensorName = orders.getDataSensorName();
        String landsatSensorName = "LC08";
        if (landsatSensorName.equalsIgnoreCase(sensorName)) {
            return dataGranuleRepository.findDataGranuleProductIdByTile(
                    orders.getDataProductName(),
                    orders.getDataSensorName(),
                    orders.getDataStartDate(),
                    orders.getDataEndDate(),
                    orders.getStartPath(),
                    orders.getEndPath(),
                    orders.getStartRow(),
                    orders.getEndRow(),
                    "TIFF"
            );
        }else {
            throw  new AssertionError("no methods for other dataSets except landsat8");
        }

    }


}
