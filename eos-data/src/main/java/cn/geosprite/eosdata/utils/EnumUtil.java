package cn.geosprite.eosdata.utils;

import cn.geosprite.eosdata.enums.CodeEnum;
import cn.geosprite.eosdata.enums.DataEnum;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 16:58 2019-4-24
 * @ Description：None
 * @ Modified By：
 */
public class EnumUtil {

    //从code得到Enum对象
    public static <T extends DataEnum> T getByCode(String code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}