package cn.geosprite.eosdata.utils;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:57 2019-4-9
 * @ Description：None
 * @ Modified By：
 */
public class UtilsTest1 {

    @Test
    public void idUpdate() {
        // LC08/L1TP_C1_T1/117050/2019-01-11 -> LC08/L1TP_C1_T1_dir/117050/2018-09-21
        String str = Utils.idUpdate("LC08/L1TP_C1_T1/117050/2019-01-11","dir");
        Assert.assertEquals("LC08/L1TP_C1_T1_DIR/117050/2019-01-11",str);

        String sr = Utils.idUpdate(str,"sr");
        Assert.assertEquals("LC08/L1TP_C1_T1_SR/117050/2019-01-11",sr);


    }

    @Test
    public void productCode(){
        String str = Utils.productCodeUpdate("L1TP_C1_T1","dir");
        Assert.assertEquals("L1TP_C1_T1_DIR",str);

        String sr = Utils.productCodeUpdate(str,"sr");
        Assert.assertEquals("L1TP_C1_T1_SR",sr);

    }

    // "raw/117/050/LC81170502019011LGN00.tgz"  ->  dir/117/050/LC81170502019011LGN00
    @Test
    public void pathUpadate(){
        String str = Utils.pathUpdate("raw/117/050/LC81170502019011LGN00.tgz","dir");
        Assert.assertEquals("dir/117/050/LC81170502019011LGN00", str);

        String sr = Utils.pathUpdate(str,"sr");
        Assert.assertEquals("sr/117/050/LC81170502019011LGN00", sr);
    }

    @Test
    public void convert(){

    }
}