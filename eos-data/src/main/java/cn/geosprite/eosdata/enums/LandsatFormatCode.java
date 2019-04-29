package cn.geosprite.eosdata.enums;

import lombok.Getter;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:19 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@Getter
public enum LandsatFormatCode implements CodeEnum {
    /**
     * TGZ表示原始数据，Format对应
     */
    TGZ("TGZ","L1TP_C1_T1"),
    //DIR means unzipped
    DIR("TIFF","L1TP_C1_T1"),
    SR("TIFF","L1TP_C1_T1_SR"),
    NDVI_TIFF("TIFF","L1TP_C1_T1_NDVI"),
    NDVI_PNG("PNG","L1TP_C1_T1_NDVI"),
    NDWI_TIFF("TIFF","L1TP_C1_T1_NDWI"),
    NDWI_PNG("PNG","L1TP_C1_T1_NDWI")
    ;

    private String format;
    private String productCode;

    LandsatFormatCode(String format, String productCode) {
        this.format = format;
        this.productCode = productCode;
    }

    public static LandsatFormatCode fromFormatName(String format) {
        if (format != null) {
            for (LandsatFormatCode type : LandsatFormatCode.values()) {
                if (type.format.equalsIgnoreCase(format)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("format参数为空");
        }
        throw new RuntimeException("format参数匹配错误");
    }

    public static LandsatFormatCode fromProductCode(String productCode) {
        if (productCode != null) {
            for (LandsatFormatCode type : LandsatFormatCode.values()) {
                if (type.productCode.equalsIgnoreCase(productCode)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("productCode参数为空");
        }
        throw new RuntimeException("format参数匹配错误");
    }

}
