package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.PathConfigs;
import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dataGranuleUtils.DataGranules;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.FormatCode;
import cn.geosprite.eosdata.service.ProcessService;
import cn.geosprite.eosprocess.service.LasrcService;
import cn.geosprite.eosprocess.service.BandMathService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProcessServiceImpl implements ProcessService {

    private PreProcessServiceImpl preProcessService;
    private BandMathService bandMathService;
    private LasrcService lasrcService;
    private DataGranuleRepository dataGranuleRepository;

    private PathConfigs pathConfigs;


    @Autowired
    public ProcessServiceImpl(PreProcessServiceImpl preProcessService, LasrcService lasrcService, BandMathService bandMathService, DataGranuleRepository dataGranuleRepository, PathConfigs pathConfigs) {
        this.preProcessService = preProcessService;
        this.lasrcService = lasrcService;
        this.bandMathService = bandMathService;
        this.dataGranuleRepository = dataGranuleRepository;
        this.pathConfigs = pathConfigs;
    }

    @Override
    public List<DataGranule> doSR(Integer orderId) {

        //返回已经做过大气校正的dataGranule信息
        List<DataGranule> result = new ArrayList<>();

        List<DataGranule> dataGranules = dataGranuleRepository.findDataGranulesByOrderDataGranuleId(orderId);

        //确保所有数据都在本地且已经解压
        List<DataGranule> dirDataGranule = preProcessService.extractFiles(dataGranules);

        for (DataGranule dataGranule: dirDataGranule){
            DataGranule outputDataGranule = DataGranules.converter(dataGranule, FormatCode.SR);

            //如果是解压后的原始数据，则需要进行大气校正
            if (dataGranule.getProductCode().equalsIgnoreCase(FormatCode.DIR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(FormatCode.DIR.getFormat())){

                //大气校正的输入路径
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                String outputPath = pathConfigs.outputPath + outputDataGranule.getDataGranulePath();

                //进行大气校正
                String log_  = lasrcService.doLasrc(inputPath, outputPath);
                log.info("atmosphere correction is done,{}", log_);
                dataGranuleRepository.save(outputDataGranule);
                dataGranule = outputDataGranule;
            }
            result.add(dataGranule);
        }
        return result;
    }

    @Override
    public List<DataGranule> doNDVI(Integer orderId) {

        /**
         *先要判断是否计算过Ndvi，然后在对其进行下一步操作。
         * 但是这里判断是否做过nvdi，逻辑应该放在doNdvi代码前。
         *
         */
        List<DataGranule> result = new ArrayList<>();

        //确保数据都做过大气校正
        List<DataGranule> list = doSR(orderId);

        for (DataGranule dataGranule: list) {
            DataGranule ndviPng = DataGranules.converter(dataGranule, FormatCode.NDVI_PNG);
            DataGranule ndviTiff = DataGranules.converter(dataGranule, FormatCode.NDVI_TIFF);

            if (dataGranule.getProductCode().equalsIgnoreCase(FormatCode.SR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(FormatCode.SR.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                String pngPath = pathConfigs.inputPath + ndviPng.getDataGranulePath();
                String tiffPath = pathConfigs.inputPath + ndviTiff.getDataGranulePath();

                log.info("start calculating ndvi for {}", dataGranule.toString());
                //这里有点问题，getNdvi应该处理多幅影像
                bandMathService.getNDVI(inputPath, pngPath, tiffPath);
                log.info("ndvi calculation has ended");
//                dataGranule = Utils.convertDataGranule(dataGranule,FormatCode.NDVI);
//                dataService.save(dataGranule);
                dataGranuleRepository.save(ndviPng);
                dataGranuleRepository.save(ndviTiff);
                result.add(ndviPng);
                result.add(ndviTiff);
            }
                result.add(dataGranule);
        }
        return result;
    }

    public List<DataGranule> doNWVI(Integer orderId){
        List<DataGranule> result = new ArrayList<>();

        //确保数据都做过大气校正
        List<DataGranule> list = doSR(orderId);

        for (DataGranule dataGranule: list) {
            DataGranule ndwiPng = DataGranules.converter(dataGranule, FormatCode.NDWI_PNG);
            DataGranule ndwiTiff = DataGranules.converter(dataGranule, FormatCode.NDWI_TIFF);

            if (dataGranule.getProductCode().equalsIgnoreCase(FormatCode.SR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(FormatCode.SR.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                String pngPath = pathConfigs.inputPath + ndwiPng.getDataGranulePath();
                String tiffPath = pathConfigs.inputPath + ndwiTiff.getDataGranulePath();

                //这里有点问题，getNdvi应该处理多幅影像
                bandMathService.getNDWI(inputPath, pngPath, tiffPath);
//                dataGranule = Utils.convertDataGranule(dataGranule,FormatCode.NDVI);
//                dataService.save(dataGranule);
                dataGranuleRepository.save(ndwiPng);
                dataGranuleRepository.save(ndwiTiff);
                result.add(ndwiPng);
                result.add(ndwiTiff);
            }
            result.add(dataGranule);
        }
        return result;
    }

}
