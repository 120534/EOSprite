package cn.geosprite.eosdata.enums;

import lombok.Getter;


/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:19 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@Getter
public enum FormatCode implements CodeEnum {
    TGZ("tgz","raw/","L1TP_C1_T1"),
    //DIR means unzipped
    DIR("dir","dir/","L1TP_C1_T1_DIR"),
    SR("sr","sr/","L1TP_C1_T1_SR"),
    NDVI("ndvi","ndvi/","L1TP_C1_T1_NDVI")
    ;

    private String format;
    private String pathPrefix;
    private String productCode;

    FormatCode(String format, String pathPrefix, String productCode) {
        this.format = format;
        this.pathPrefix = pathPrefix;
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
