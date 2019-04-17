package cn.geosprite.eosdata.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:19 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@Getter
@Slf4j
public enum FormatCode implements CodeEnum {
    TGZ("TGZ","L1TP_C1_T1"),
    //DIR means unzipped
    DIR("TIFF","L1TP_C1_T1"),
    SR("TIFF","L1TP_C1_T1_SR"),
    NDVI_TIFF("TIFF","L1TP_C1_T1_NDVI"),
    NDVI_PNG("PNG","L1TP_C1_T1_NDVI")
    ;

    private String format;
    private String productCode;

    FormatCode(String format,  String productCode) {
        this.format = format;
        this.productCode = productCode;
    }


    public static FormatCode fromTypeName(String format) {
        if (format != null) {
            for (FormatCode type : FormatCode.values()) {
                if (type.format.equalsIgnoreCase(format)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("format参数为空");
        }
            return null;
    }

}
