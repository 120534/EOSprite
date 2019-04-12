package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.PathConfigs;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import cn.geosprite.eosdata.service.ProcessService;
import cn.geosprite.eosdata.utils.LasrcService;
import cn.geosprite.eosdata.utils.Utils;
import cn.geosprite.eosprocess.service.BandMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 21:14 2019-4-7
 * @ Description：None
 * @ Modified By：
 */

@Service
public class ProcessServiceImpl implements ProcessService {

    private DataServiceImpl dataService;
    private PathConfigs pathConfigs;
    private BandMathService bandMathService;

    @Autowired
    public ProcessServiceImpl(DataServiceImpl dataService, PathConfigs pathConfigs, BandMathService bandMathService){
        this.dataService = dataService;
        this.pathConfigs = pathConfigs;
        this.bandMathService = bandMathService;
    }

    @Override
    public List<DataGranule> doSR(List<DataGranule> dataGranules) {

        //返回已经做过大气校正的dataGranule信息
        List<DataGranule> srDataGranules = new ArrayList<>();

        //确保所有数据都在本地且已经解压
        List<DataGranule> dirDataGranule = unzip(dataGranules);

        for (DataGranule dataGranule: dirDataGranule){

            //如果ID里面不包含SR，则需要进行大气校正
            if (!dataGranule.getDataGranuleId().contains("SR")){
                //大气校正的输入路径
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();

                //进行大气校正
                LasrcService.doLasrc(inputPath);

                //大气校正后的数据信息入库
                dataGranule = Utils.convertDataGranule(dataGranule, FormatCode.SR);
                dataService.save(dataGranule);

                srDataGranules.add(dataGranule);
            }else{
                //数据已经做过大气校正, 直接添加到list
                srDataGranules.add(dataGranule);
            }
        }
        return srDataGranules;
    }

    /**
     * 对dataGranule判断其是否有未解压的文件，对齐进行解压，同时把产生的新的dataGranule写入到数据库
     * @param tgzList
     * @return
     */
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
        List<DataGranule> dir = new ArrayList<>();

        for (DataGranule dataGranule: tgzList){
            //获取dataGranul里面可能需要修改的信息
            String dataGranulePath = dataGranule.getDataGranulePath();
            String formatCode = dataGranule.getFormatCode();
            String uri = dataGranule.getDataGranuleUri();

            if (uri == null){
                //判断文件是否被压缩
                if (formatCode.equalsIgnoreCase(FormatCode.TGZ.getFormat())){
                    //数据在本地的真实路径
                    String inputPath = pathConfigs.inputPath + dataGranulePath;
                    //数据输出路径设置,进行字符串拼接，
                    // /mnt/disk1/geodata/  +  dir/117/050/LC81170502019011LGN00
                    String outPath = pathConfigs.outputPath  + Utils.pathUpdate(dataGranule.getDataGranulePath(),"dir");
                    //解压数据
                    Utils.unzip(inputPath, outPath);

                    //解压后的数据信息入库
                    dataGranule = Utils.convertDataGranule(dataGranule, FormatCode.DIR);
                    dataService.save(dataGranule);
                    dir.add(dataGranule);
                }else {
                    //如果没有被压缩，直接放入list返回。
                    dir.add(dataGranule);
                }
            }else {
                //后期补充
                DataGranule dataGranule1 = downloadData(dataGranule);
                dir.add(dataGranule);
            }
        }
            return dir;
        }

    @Override
    public List<DataGranule> doNDVI(List<DataGranule> srList) {

        /**
         *先要判断是否计算过Ndvi，然后在对其进行下一步操作。
         * 但是这里判断是否做过nvdi，逻辑应该放在doNdvi代码前。
         *
         */


        List<DataGranule> ndviDataGranules = new ArrayList<>();

        //确保数据都做过大气校正
        List<DataGranule> list = doSR(srList);

        for (DataGranule dataGranule: list) {
            if (!dataGranule.getFormatCode().equalsIgnoreCase(FormatCode.NDVI.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                //这里有点问题，getNdvi应该处理多幅影像
                bandMathService.getNdvi(inputPath);
                dataGranule = Utils.convertDataGranule(dataGranule,FormatCode.NDVI);
                dataService.save(dataGranule);
                ndviDataGranules.add(dataGranule);
            }else {
                ndviDataGranules.add(dataGranule);
            }
        }
        return ndviDataGranules;
    }

    /**
     * 预留接口，判断URI是否为空，提供数据下载到本地，同时写入数据库。
     * @param data
     * @return
     */

    public DataGranule downloadData(DataGranule data) {
        return null;
    }




}
