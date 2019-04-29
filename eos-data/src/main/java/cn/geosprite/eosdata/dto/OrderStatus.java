package cn.geosprite.eosdata.dto;

import cn.geosprite.eosdata.entity.DataGranule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.util.List;

/**
 * @ Date       ：Created in 13:52 2019-4-29
 * @ Description：None
 * @ Modified By：
 * @author 17491
 */
@Data
@Accessors(chain = true)
public class OrderStatus {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp orderCompletedTime;
    private String message;
    private List<DataGranule> dataGranuleList;


}
