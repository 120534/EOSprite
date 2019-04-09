package cn.geosprite.eosdata.utils;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import cn.geosprite.eosdata.service.impl.DataServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:40 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {

    @Autowired
    DataServiceImpl dataService ;
    @Test
    public void convertDataGranule() {
        List<DataGranule> list = dataService.findDataGranulesByOrderId(1);

        // dir -> sr时候，应该把dir表示给去除掉。
        for (DataGranule ds : list){
        DataGranule dir = Utils.convertDataGranule(ds,FormatCode.DIR);
        dataService.save(dir);
        DataGranule sr = Utils.convertDataGranule(dir,FormatCode.SR);
        dataService.save(sr);
        }
    }

    @Test
    public void dataGranule() {
        List<DataGranule> list = dataService.findDataGranulesByOrderId(1);

        // dir -> sr时候，应该把dir表示给去除掉。
        List<DataGranule> dirs = new ArrayList<>();
        for (int i = 0 ;i < list.size(); i++) {
            DataGranule dataGranule = list.get(i);
            dataGranule = Utils.convertDataGranule(dataGranule,FormatCode.NDVI);
            dirs.add(dataGranule);
        }

        list.forEach(x -> System.out.println(x.toString()));
        System.out.println("----------------");
        dirs.forEach(x -> System.out.println(x.toString()));


    }
}