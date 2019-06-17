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

    /**这个Enum存储了数据的模型，以及其对应的格式
     * 每一个数据产品都有一个对应的数据格式
     * TGZ表示原始数据，Format对应
     * productCode -> format
     * 用于后端存储，
     */
    TGZ("TGZ","L1TP_C1_T1"),
    //DIR means unzipped
    DIR("TIFF","L1TP_C1_T1"),
    SR("TIFF","L1TP_C1_T1_SR"),
    NDVI_TIFF("TIFF","L1TP_C1_T1_NDVI"),
    NDVI_PNG("PNG","L1TP_C1_T1_NDVI"),
    NDWI_TIFF("TIFF","L1TP_C1_T1_NDWI"),
    NDWI_PNG("PNG","L1TP_C1_T1_NDWI"),
    /**DDI based on Albedo-NDVI*/
    DDIAN_TIFF("TIFF","L1TP_C1_T1_DDIAN"),
    DDIAN_PNG("PNG","L1TP_C1_T1_DDIAN")
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
