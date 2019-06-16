package cn.geosprite.eosdata.dto;

import cn.geosprite.eosdata.entity.Orders;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 14:29 2019-4-25
 * @ Description：由Order对象转换得到，用于前端Orders.HTML主页面显示的
 * @ Modified By：
 */
@Data
@Accessors(chain = true)
public class OrderOutputDTO {

    private Integer orderId;
    private String dataSensorName;
    private String dataProductName;

    private Integer startPath;
    private Integer endPath;
    private Integer startRow;
    private Integer endRow;

    private java.sql.Date dataStartDate;
    private java.sql.Date dataEndDate;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp orderSubmittedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp orderCompletedTime;

}
