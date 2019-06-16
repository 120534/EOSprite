package cn.geosprite.eosdata.dataGranuleUtils;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.LandsatFormatCode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:17 2019-4-15
 * @ Description：None
 * @ Modified By：
 */
@Slf4j
public class DataGranules {

    /**  LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11		LC08	L1TP_C1_T1	117/050	2019-01-11	TGZ		USGS	LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz
         LC08/L1TP_C1_T1/TIFF/117/050/2019/01/11	LC08	L1TP_C1_T1	117/050	2019-01-11	TIFF	USGS	LC08/L1TP_C1_T1/TIFF/117/050/2019/01/11/LC81170502019011LGN00
         LC08/L1TP_C1_T1_SR/TIFF/117/050/2019/01/11	LC08	L1TP_C1_T1_SR	117/050	2019-01-11	TIFF	USGS	LC08/L1TP_C1_T1_SR/TIFF/117/050/2019/01/11/LC81170502019011LGN00
         LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11	LC08	L1TP_C1_T1_NDVI	117/050	2019-01-11	TIFF	USGS	LC08/L1TP_C1_T1_NDVI/TIFF/117/050/2019/01/11/LC81170502019011LGN00.TIF
         LC08/L1TP_C1_T1_NDVI/PNG/117/050/2019/01/11	LC08	L1TP_C1_T1_NDVI	117/050	2019-01-11	PNG	        USGS	LC08/L1TP_C1_T1_NDVI/PNG/117/050/2019/01/11/LC81170502019011LGN00.PNG
     * 实现不同类型的dataGranule之间转换,
     * 一共涉及到的转换包括 TGZ -> DIR , DIR -> SR , SR -> (NDVI, ALBEDO, etc...)
     * 在进行TGZ -> DIR 转换时候，需要变动的有：
     * 1.ID  LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11 -> LC08/L1TP_C1_T1/DIR/117/050/2019/01/11
     * 2.dataFormat TGZ -> TIFF
     * 3.
     * @param dataGranule
     * @param fc
     * @return
     */
    public static DataGranule converter(DataGranule dataGranule, LandsatFormatCode fc){
//  LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11		LC08	L1TP_C1_T1	117/050	2019-01-11	 TGZ  USGS	LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz
//  LC08/L1TP_C1_T1_SR/DIR/117/050/2018/12/26        LC08      L1TP_C1_T1_SR	117/050	2018-12-26	TIFF  USGS	LC08/L1TP_C1_T1_SR/DIR/117/050/2018/12/26/LC81170502018360LGN00

        //先判断目标和现有类型是否一致，如果相同则不转换。
        if (!dataGranule.getProductCode().equalsIgnoreCase(fc.getProductCode()) ||
                !dataGranule.getFormatCode().equalsIgnoreCase(fc.getFormat())){
            String desFormat = fc.getFormat();
            String desProductCode = fc.getProductCode();
            String desId = convertId(dataGranule, fc);
            String desPath = convertPath(dataGranule, fc);
            //需要修改
            String srcId = dataGranule.getDataGranuleId();
            String srcSensor = dataGranule.getSensorCode();
            //需要修改
            String srcProductCode = dataGranule.getProductCode();
            //需要修改
            String srcFormat = dataGranule.getFormatCode();
            String srcTile = dataGranule.getTileCode();
            java.sql.Date srcDate = dataGranule.getSceneDate();
            String srcSource = dataGranule.getDataSource();
            //需要修改
            String srcPath = dataGranule.getDataGranulePath();
            String srcUri = dataGranule.getDataGranuleUri();

            dataGranule = new DataGranule(desId, srcSensor, desProductCode, srcTile, srcDate, desFormat, srcSource, desPath, srcUri);
        }else {
            log.warn("DataGranule converting error,same format for src and des {}", fc.getFormat());
        }
        return dataGranule;
    }


    private static String convertId(DataGranule dataGranule, LandsatFormatCode fc){
        String srcId = dataGranule.getDataGranuleId();
        String srcProductCode = dataGranule.getProductCode();
        String srcDataFormat = dataGranule.getFormatCode();
        return srcId.replace(srcDataFormat, fc.getFormat()).replace(srcProductCode, fc.getProductCode());
    }

    /** 生成path，这个path转换包括三种情况，TGZ -> DIR , DIR -> SR , SR -> (NDVI, ALBEDO, etc...)
     * 后期还需要添加 (NDVI, ALBEDO, etc...) -> Others
     * @param dataGranule
     * @param fc
     * @return
     */

    private static String convertPath(DataGranule dataGranule, LandsatFormatCode fc){
        //原始数据信息
        String path = dataGranule.getDataGranulePath();
        List<String> list = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(path);
        String dataName = list.get(list.size() - 1);
        String id = dataGranule.getDataGranuleId();
        String formatCode = dataGranule.getFormatCode();
        /** 如 L1TP_C1_T1_NDVI */
        String productCode = dataGranule.getProductCode();
        /** 如 NDVI */
        List<String> names = Splitter.on("_").trimResults().omitEmptyStrings().splitToList(fc.getProductCode());
        String productName = names.get(names.size() - 1);

        //LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz
        //
        // 在修改文件名的时候需要考虑到，原始数据存储的格式，是文件夹还是带有后缀。
        // 同时还要考虑到转换的类型是文件夹还是其他数据。 TGZ -> DIR , DIR -> SR , SR -> (NDVI, ALBEDO, etc...)
        // 一共三种情况，TGZ -> DIR 去掉后缀， DIR -> SR 不用改变， SR -> 其他产品 添加后缀

        if (productCode.equalsIgnoreCase(LandsatFormatCode.TGZ.getProductCode()) && formatCode.equalsIgnoreCase(LandsatFormatCode.TGZ.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc)).replace(dataName, dataName.substring(0, dataName.length() - 4));
        }else if (productCode.equalsIgnoreCase(LandsatFormatCode.DIR.getProductCode())&& formatCode.equalsIgnoreCase(LandsatFormatCode.DIR.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc));
        }else if (productCode.equalsIgnoreCase(LandsatFormatCode.SR.getProductCode())&& formatCode.equalsIgnoreCase(LandsatFormatCode.SR.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc)).replace(dataName, dataName + "_" + productName+"." + fc.getFormat().toLowerCase());
        }else {
            log.error("No format code matched for {}", dataGranule.toString());
            throw new RuntimeException("No format code matched");
        }
        return path;
    }

    /**从数据产品DataGranule到原始数据DataGranule，目前只适用于Landsat*/
    public static DataGranule converterBack(DataGranule dataGranule, LandsatFormatCode fc){
//  LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11		LC08	L1TP_C1_T1	117/050	2019-01-11	 TGZ  USGS	LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz
        /**
         * 需要修改的包括 id， Product, dataFormat, path
         */
        String dataGranuleId = dataGranule.getDataGranuleId();
        String path = dataGranule.getDataGranulePath();


        String newId = convertId(dataGranule,fc);
        String newProductCode = fc.getProductCode();
        String newFormatCode = fc.getFormat();

        //LC08/L1TP_C1_T1_NDVI/TIFF/117/043/2018/03/29/LC81170432018088LGN00_NDVI.TIFF
        //LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz

        List<String> list = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(path);
        String dataName = list.get(list.size() - 1);

        List<String> names = Splitter.on("_").trimResults().omitEmptyStrings().splitToList(dataName);
        String newName = names.get(0) + "." + fc.getFormat().toLowerCase();
        String newPath = path.replace(dataGranuleId,newId)
                .replace(dataName,newName);

        return  new DataGranule()
                .setDataGranuleId(newId)
                .setProductCode(newProductCode)
                .setDataGranulePath(newPath)
                .setTileCode(dataGranule.getTileCode())
                .setDataSource(dataGranule.getDataSource())
                .setFormatCode(fc.getFormat())
                .setSensorCode(dataGranule.getSensorCode())
                .setSceneDate(dataGranule.getSceneDate());
    }

    /**
     * 从原始数据直接到数据产品DataGranule
     LC08/L1TP_C1_T1/TGZ/113/023/2018/01/12		L1TP_C1_T1		TGZ		LC08/L1TP_C1_T1/TGZ/113/023/2018/01/12/LC81130232018012LGN00.tgz
     LC08/L1TP_C1_T1_NDVI/TIFF/117/043/2018/03/29		L1TP_C1_T1_NDVI			TIFF		LC08/L1TP_C1_T1_NDVI/TIFF/117/043/2018/03/29/LC81170432018088LGN00_NDVI.TIFF
     * */

    public static DataGranule converterForward(DataGranule dataGranule, LandsatFormatCode fc){
        String oldId = dataGranule.getDataGranuleId();
        String oldFormat = dataGranule.getFormatCode();
        String oldProduct = dataGranule.getProductCode();
        String oldPath = dataGranule.getDataGranulePath();

        String newId = convertId(dataGranule,fc);
        String newFormat = fc.getFormat();
        String newProduct = fc.getProductCode();

        /**On Arrays.asList returning a fixed-size list*/
        List<String> list = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(oldPath);
        /**获取到数据的名称 LC81130232018012LGN00.tgz*/
        String name = list.get(list.size() - 1);
        /**得到LC81130232018012LGN00*/
        List<String> names = Splitter.on(".").trimResults().omitEmptyStrings().splitToList(name);
        String dataName = names.get(0);
        /**L1TP_C1_T1_NDVI -> NDVI*/
        List<String> type = Splitter.on("_").trimResults().omitEmptyStrings().splitToList(newProduct);

        Joiner.on("_").skipNulls().join(names);

        String newName = dataName + "_" + type.get(type.size()-1) + "." + fc.getFormat().toLowerCase();

        String newPath = oldPath.replace(oldId, newId).replace(name, newName);

        return new DataGranule()
                .setDataGranuleId(newId)
                .setProductCode(newProduct)
                .setDataGranulePath(newPath)
                .setTileCode(dataGranule.getTileCode())
                .setDataSource(dataGranule.getDataSource())
                .setFormatCode(fc.getFormat())
                .setSensorCode(dataGranule.getSensorCode())
                .setSceneDate(dataGranule.getSceneDate());
    }

}
