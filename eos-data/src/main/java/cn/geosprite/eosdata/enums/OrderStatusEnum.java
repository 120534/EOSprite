package cn.geosprite.eosdata.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 20:22 2019-4-25
 * @ Description：None
 * @ Modified By：
 */
@Getter
@Slf4j
public enum OrderStatusEnum {
    /**
     * code是数据库中存储的代码，message存储其表达的信息
     */
    NEW(0, "新订单"),
    FINISHED(1, "订单已完成"),
    CANCEL(2, "订单已取消"),
    ERROR(3, "订单出错")
    ;
    private Integer code;
    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMassageFromCode(Integer code){
        for (OrderStatusEnum status: OrderStatusEnum.values()){
            if (code.equals(status.getCode())){
                return status.getMessage();
            }
        }
        log.error("Code {} is not in orderStatus", code);
        return  null;
    }
}
