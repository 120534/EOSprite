package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.PathConfigs;
import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import cn.geosprite.eosdata.service.PreProcessService;
import cn.geosprite.eosdata.service.ProcessService;
import cn.geosprite.eosprocess.service.LasrcService;
import cn.geosprite.eosprocess.service.BandMathService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
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

    private PreProcessService preProcessService;
    private BandMathService bandMathService;
    private LasrcService lasrcService;
    private DataGranuleRepository dataGranuleRepository;

    private PathConfigs pathConfigs;


    @Autowired
    public ProcessServiceImpl(PreProcessService preProcessService, LasrcService lasrcService, BandMathService bandMathService, DataGranuleRepository dataGranuleRepository, PathConfigs pathConfigs) {
        this.preProcessService = preProcessService;
        this.lasrcService = lasrcService;
        this.bandMathService = bandMathService;
        this.dataGranuleRepository = dataGranuleRepository;
        this.pathConfigs = pathConfigs;
    }

    @Override
    public List<DataGranule> doSR(Integer orderId) {

        //返回已经做过大气校正的dataGranule信息
        List<DataGranule> srDataGranules = new ArrayList<>();

        List<DataGranule> dataGranules = dataGranuleRepository.findDataGranulesByOrderDataGranuleId(orderId);

        //确保所有数据都在本地且已经解压
        List<DataGranule> dirDataGranule = preProcessService.extractFiles(dataGranules);

        for (DataGranule dataGranule: dirDataGranule){

            DataGranule outputDataGranule;

            //如果ID里面不包含SR，则需要进行大气校正
            if (!dataGranule.getDataGranuleId().contains("SR")){
                //大气校正的输入路径
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();

                //进行大气校正
                String outputPath = lasrcService.doLasrc(inputPath);

                String dataGranuleId = outputPath;
                outputDataGranule = new DataGranule();

                try {
                    BeanUtils.copyProperties(outputDataGranule, dataGranule);
                    dataGranule.setDataGranulePath(outputPath);
                    dataGranule.setDataGranuleId(dataGranuleId);
                    dataGranuleRepository.save(outputDataGranule);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                outputDataGranule = dataGranule;
            }

            srDataGranules.add(outputDataGranule);
        }
        return srDataGranules;
    }



    @Override
    public List<DataGranule> doNDVI(Integer orderId) {

        /**
         *先要判断是否计算过Ndvi，然后在对其进行下一步操作。
         * 但是这里判断是否做过nvdi，逻辑应该放在doNdvi代码前。
         *
         */
        List<DataGranule> ndviDataGranules = new ArrayList<>();

        //确保数据都做过大气校正
        List<DataGranule> list = doSR(orderId);

        for (DataGranule dataGranule: list) {

            if (!dataGranule.getFormatCode().equalsIgnoreCase(FormatCode.NDVI.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                //这里有点问题，getNdvi应该处理多幅影像
                bandMathService.getNdvi(inputPath);
//                dataGranule = Utils.convertDataGranule(dataGranule,FormatCode.NDVI);
//                dataService.save(dataGranule);
                ndviDataGranules.add(dataGranule);
            }else {
                ndviDataGranules.add(dataGranule);
            }
        }
        return ndviDataGranules;
    }
}
