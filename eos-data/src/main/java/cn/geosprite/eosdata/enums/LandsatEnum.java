package cn.geosprite.eosdata.enums;

import lombok.Getter;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 15:46 2019-4-24
 * @ Description：None
 * @ Modified By：
 */
@Getter
public enum LandsatEnum implements DataEnum {
    /**
     * productCode -> name
     * 用于前端的匹配
     * */
    /**配对数据集类型*/
    LANDSAT8_SENSOR("LC08","Landsat-8"),
    /**匹配前端，将前端请求的“归一化植被指数(NDVI)”转化为 “L1TP_C1_T1_NDVI”
     * 将后端存储的“L1TP_C1_T1_NDVI”返回前段时转化为 “归一化植被指数(NDVI)”
     * */
    LANDSAT8_NDVI("L1TP_C1_T1_NDVI","归一化植被指数(NDVI)"),
    LANDSAT8_NDWI("L1TP_C1_T1_NDWI","归一化水体指数(NDWI)"),
    LANDSAT8_SR("L1TP_C1_T1_SR","地表反射率(SR)"),
    LANDSAT8_KMEANS("L1TP_C1_T1_SR","非监督分类(K-Means)"),
    /**荒漠化信息提取模型Albedo-NDVI，差值指数。*/
    LANDSAT8_DDIAN("L1TP_C1_T1_DDIAN","荒漠化信息提取模型(Albedo-NDVI)")
    ;

    private String code;
    private String name;

    LandsatEnum(String code,String name) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    /**
     * 从order name 得到OrderEnum
     */
    public static LandsatEnum fromName(String name) {
        if (name != null) {
            for (LandsatEnum type : LandsatEnum.values()) {
                if (type.name.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("name参数为空");
        }
        return null;
    }

    public static LandsatEnum fromCode(String code) {
        if (code != null) {
            for (LandsatEnum type : LandsatEnum.values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("code参数为空");
        }
        return null;
    }

}
