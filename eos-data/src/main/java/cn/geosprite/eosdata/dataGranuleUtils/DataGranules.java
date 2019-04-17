package cn.geosprite.eosdata.dataGranuleUtils;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
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
    public static DataGranule converter(DataGranule dataGranule, FormatCode fc){
//  LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11		LC08	L1TP_C1_T1	117/050	2019-01-11	 TGZ  USGS	LC08/L1TP_C1_T1/TGZ/117/050/2019/01/11/LC81170502019011LGN00.tgz
//  LC08/L1TP_C1_T1_SR/DIR/117/050/2018/12/26	LC08      L1TP_C1_T1_SR	117/050	2018-12-26	TIFF  USGS	LC08/L1TP_C1_T1_SR/DIR/117/050/2018/12/26/LC81170502018360LGN00

        //先判断目标和现有类型是否一致，如果相同则报错。
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
            log.error("DataGranule converting error,same format for src and des {}", fc.getFormat());
            throw new RuntimeException("dataGranule converting error");
        }
        return dataGranule;
    }

    private static String convertId(DataGranule dataGranule, FormatCode fc){
        String srcId = dataGranule.getDataGranuleId();
        String srcProductCode = dataGranule.getProductCode();
        String srcDataFormat = dataGranule.getFormatCode();
        return srcId.replace(srcDataFormat,fc.getFormat()).replace(srcProductCode, fc.getProductCode());
    }

    /** path里面需要替换id，以及文件名。
     * @param dataGranule
     * @param fc
     * @return
     */
    private static String convertPath(DataGranule dataGranule, FormatCode fc){
        String path = dataGranule.getDataGranulePath();
        List<String> list = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(path);
        String dataName = list.get(list.size() - 1);
        String id = dataGranule.getDataGranuleId();
        String formatCode = dataGranule.getFormatCode();
        String productCode = dataGranule.getProductCode();

        // 在修改文件名的时候需要考虑到，原始数据存储的格式，是文件夹还是带有后缀。
        // 同时还要考虑到转换的类型是文件夹还是其他数据。 TGZ -> DIR , DIR -> SR , SR -> (NDVI, ALBEDO, etc...)
        // 一共三种情况，TGZ -> DIR 去掉后缀， DIR -> SR 不用改变， SR -> 其他产品 添加后缀

        if (productCode.equalsIgnoreCase(FormatCode.TGZ.getProductCode()) && formatCode.equalsIgnoreCase(FormatCode.TGZ.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc)).replace(dataName, dataName.substring(0, dataName.length() - 4));
        }else if (productCode.equalsIgnoreCase(FormatCode.DIR.getProductCode())&& formatCode.equalsIgnoreCase(FormatCode.DIR.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc));
        }else if (productCode.equalsIgnoreCase(FormatCode.SR.getProductCode())&& formatCode.equalsIgnoreCase(FormatCode.SR.getFormat())){
            path = path.replace(id, convertId(dataGranule, fc)).replace(dataName, dataName+ "." + fc.getFormat());
        }else {
            log.error("No format code matched for {}", dataGranule.toString());
            throw new RuntimeException("No format code matched");
        }
        return path;
    }
}
