package cn.geosprite.eosdata.dto;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.enums.LandsatEnum;
import cn.geosprite.eosdata.enums.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 14:59 2019-4-18
 * @ Description：接受前端Tile path and row查询，封装对象
 * @ Modified By：
 */
//"select * from data_granules as a where a.product_code = ?1 and a.scene_date between ?2 and ?3 and sensor_code = ?4 and tile_code;
//    ""ImageInfo(Landsat-8, 123, 12, 01/11/2018 - 06/15/2018, Tier-1)"
@Accessors(chain = true)
@Data
public class OrderInputDTO {
    /**
     * 前端数据示例：
     *
     * sensorCode: Landsat-8
     * productCode: 归一化水体指数(NDWI)
     * startPath: 122
     * endPath: 123
     * startRow: 1
     * endRow: 34
     * date: 01/11/2018 - 02/21/2018
     */
    private String dataSensorName;
    private String dataProductName;

    private Integer startPath;
    private Integer endPath;

    private Integer startRow;
    private Integer endRow;

    private String date;

    @JsonIgnore
    private Date dataStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String[] str = date.trim().split("-");
        if (str.length == 0){
            throw new RuntimeException("OrderInputDTO.date can not be null");
        }
        Date date = null;
        try {
            date =  new Date(sdf.parse(str[0]).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @JsonIgnore
    private Date dataEndDate()  {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String[] str = date.trim().split("-");
        if (str.length == 0){
            throw new RuntimeException("OrderInputDTO.date can not be null");
        }
        Date date = null;
        try {
            date =  new Date(sdf.parse(str[1]).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @JsonIgnore
    public Orders converToOrders(){
        OrderInputDTOConvert orderInputDTOConvert = new OrderInputDTOConvert();
        return orderInputDTOConvert.doForward(this);
    }

    @JsonIgnore
    public OrderInputDTO convertToOrderDTO(Orders orders){
        OrderInputDTOConvert orderInputDTOConvert = new OrderInputDTOConvert();
        return orderInputDTOConvert.doBackward(orders);
    }

    @JsonIgnore
    public OrderOutputDTO convertToOrderOutputDTO(Orders orders){
        OrderInputDTOConvert orderInputDTOConvert = new OrderInputDTOConvert();
        return orderInputDTOConvert.convertToVO(this, orders);
    }

    private static class OrderInputDTOConvert implements DTOConvert<OrderInputDTO, Orders> {
        /**
         * 将前端order form 转为 Orders对象，方便存储到数据库中
         * @param orderInputDTO
         * @return
         */
        @Override
        public Orders doForward(OrderInputDTO orderInputDTO) {
            Orders order = new Orders();
            /**
             * 存储在数据库中都是使用code来表示
             */
            String productName = orderInputDTO.getDataProductName();
            String sensorName = orderInputDTO.getDataSensorName();

            // TODO:需要判断是哪种影像
            LandsatEnum product= LandsatEnum.fromName(productName);
            LandsatEnum sensor= LandsatEnum.fromName(sensorName);
            Integer startPath = orderInputDTO.getStartPath();
            Integer endPath = orderInputDTO.getEndPath();
            Integer startRow = orderInputDTO.getStartRow();
            Integer endRow = orderInputDTO.getEndRow();

            order.setDataStartDate(orderInputDTO.dataStartDate())
                    .setOrderSubmittedTime(new Timestamp(System.currentTimeMillis()))
                    .setDataEndDate(orderInputDTO.dataEndDate())
                    .setDataSensorName(sensor.getCode())
                    .setDataProductName(product.getCode())
                    .setStartPath(tileConvert(startPath))
                    .setEndPath(tileConvert(endPath))
                    .setStartRow(tileConvert(startRow))
                    .setEndRow(tileConvert(endRow))
                    .setStatus(0);
            //处理path或者row数值，将其装换为String类型

            return order;
        }

        @Override
        public OrderInputDTO doBackward(Orders orders) {
            throw  new AssertionError("doBackward is not allowed for OrderInputDTO");
        }

        /**
         * 用来对tile path和row进行补全，如 path = 1,tileConvert(path), return "100";
         * @param i
         * @return
         */
        String tileConvert(Integer i){
            StringBuilder s  = new StringBuilder(i.toString());
            if (s.length() < 3){
                for (int y = 0; y <= 3 - s.length();y++){
                    s.insert(0,"0");
                }
            }
            return s.toString();
        }

        OrderOutputDTO convertToVO(OrderInputDTO orderInputDTO, Orders orders){
            OrderOutputDTO orderOutputDTO = new OrderOutputDTO();
            //这里的order应该从数据库中读出来。
            /**
             * orderInputDTO -> orderOutputDTO
             * 传入属性不需要修改的有
             *  1.orderOutputDTO
             *  2.dataProductName
             *  3.startPath
             *  4.endPath
             *  5.startRow
             *  6.endRow
             *
             *  orders -> orderOutputDTO
             *  需要其传入的属性有
             *  1.orderId
             *  2.dataStartDate
             *  3.dataEndDate
             *  4.orderStatus(这个后面通过ajax访问进行修改)
             */
            BeanUtils.copyProperties(orderInputDTO, orderOutputDTO);
            orderOutputDTO
                    .setOrderId(orders.getOrderId())
                    .setDataStartDate(orders.getDataStartDate())
                    .setDataEndDate(orders.getDataEndDate())
                    .setOrderSubmittedTime(orders.getOrderSubmittedTime())
                    .setOrderCompletedTime(orders.getOrderCompletedTime())
                    .setStatus(OrderStatusEnum.getMassageFromCode(orders.getStatus()));

            return orderOutputDTO;
        }
    }

}
