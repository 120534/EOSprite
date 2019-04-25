package cn.geosprite.eosdata.enums;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 15:31 2019-4-24
 * @ Description：将orders name与dataGranule code相对应。
 * @ Modified By：
 */
public enum OrderEnum implements DataEnum {
    LANDSAT8_SENSOR("Landsat-8","LC8"),
    LANDSAT8_NDVI("L1TP_C1_T1_NDVI","归一化植被指数(NDVI)"),
    LANDSAT8_NDWI("L1TP_C1_T1_NDWI","归一化水体指数(NDWI)")
    ;

    private String name;
    private String code;

    OrderEnum(String name, String code) {
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
    public static OrderEnum fromName(String name) {
        if (name != null) {
            for (OrderEnum type : OrderEnum.values()) {
                if (type.name.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("name参数为空");
        }
        return null;
    }

    public static OrderEnum fromCode(String code) {
        if (code != null) {
            for (OrderEnum type : OrderEnum.values()) {
                if (type.name.equalsIgnoreCase(code)) {
                    return type;
                }
            }
        }else{
            throw new RuntimeException("code参数为空");
        }
        return null;
    }
}
