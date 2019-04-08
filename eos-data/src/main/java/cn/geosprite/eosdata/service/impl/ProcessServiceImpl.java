package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.Configs;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import cn.geosprite.eosdata.service.ProcessService;
import cn.geosprite.eosdata.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:14 2019-4-7
 * @ Description：None
 * @ Modified By：
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    private DataServiceImpl dataService;
    private Configs configs;

    @Autowired
    public ProcessServiceImpl(DataServiceImpl dataService, Configs configs){
        this.dataService = dataService;
        this.configs = configs;
    }

    @Override
    public String doLasrc(Integer orderId) {

        List<DataGranule> dataGranules = dataService.findDataGranulesByOrderId(orderId);

        String[] msg;

        List<DataGranule> dirDataGranules = new ArrayList<>();

        //确保所有数据都在本地
        List<DataGranule> unzip = unzip(dirDataGranules);

        //map中每个entity对应，lasrc的输入和输出路径，lasrc(key，value)
        Map<String ,String> pathMap = new HashMap<>();

        for (DataGranule dataGranule: unzip){

            String inputPath = configs.inputPath + dataGranule.getDataGranulePath();

            //这里需要提供
            String outputPath = configs.outputPath + dataGranule.getDataGranulePath();

        }

        return null;
    }

    /**
     * 对dataGranule判断其是否有未解压的文件，对齐进行解压，同时把产生的新的dataGranule写入到数据库
     * @param tgzList
     * @return
     */
    @Override
    public List<DataGranule> unzip(List<DataGranule> tgzList)  {

        /**
         * 得到orderId对应的dataGranules
         * 遍历dataGranules
         * 1.判断文件是否为本地，条件: uri是否为空，不为空则下载（预留相关处理接口）
         * 2.uri为空，则表明数据在本地，判断数据是否为压缩格式，条件：formatCode.equals("tgz")
         *      一.如果是tgz格式则进行解压缩，同时将新的解压数据信息写入表。
         *      二.如果不是tgz格式（则为unzipped），进行本地数据读取。
         *
         */

        for (DataGranule dataGranule: tgzList){
            //获取dataGranul里面可能需要修改的信息
            String dataGranulePath = dataGranule.getDataGranulePath();
            String formatCode = dataGranule.getFormatCode();
            String uri = dataGranule.getDataGranuleUri();

            if (uri == null){
                //判断文件是否被压缩
                if (formatCode.equalsIgnoreCase(FormatCode.TGZ.getFormat())){
                    //数据在本地的真实路径
                    String inputPath = configs.inputPath + dataGranulePath;
                    //数据输出路径设置,进行字符串拼接，
                    // /mnt/disk1/geodata/  +  dir/117/050/LC81170502019011LGN00
                    String outPath = configs.outputPath  + Utils.pathUpdate(dataGranule.getDataGranulePath(),"dir");
                    //解压数据
                    Utils.unzip(inputPath, outPath);

                    //解压后的数据信息入库
                    dataGranule = Utils.convertDataGranule(dataGranule,"dir");
                    dataService.save(dataGranule);
                }
            }else {
                //后期补充
                downloadData(dataGranule);
            }
        }
            return tgzList;
        }

    /**
     * 预留接口，判断URI是否为空，提供数据下载到本地，同时写入数据库。
     * @param data
     * @return
     */
    @Override
    public DataGranule downloadData(DataGranule data) {
        return null;
    }


}
