package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.PathConfigs;
import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dataGranuleUtils.DataGranules;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.enums.LandsatFormatCode;
import cn.geosprite.eosdata.service.PreProcessService;
import lombok.extern.slf4j.Slf4j;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 16:37 2019-4-12
 * @ Description：None
 * @ Modified By：
 */
@Service
@Slf4j
public class PreProcessServiceImpl implements PreProcessService {

    private PathConfigs pathConfigs;
    private DataGranuleRepository dataGranuleRepository;

    @Autowired
    public PreProcessServiceImpl(DataGranuleRepository dataGranuleRepository, PathConfigs pathConfigs) {
        this.dataGranuleRepository = dataGranuleRepository;
        this.pathConfigs = pathConfigs;
    }

    @Override
    public DataGranule downloadData(DataGranule dataGranule) {
        return null;
    }

    @Override
    public List<DataGranule> extractFiles(List<DataGranule> dataGranules) {
        /**
         * 得到orderId对应的dataGranules
         * 遍历dataGranules
         * 1.判断文件是否为本地，条件: uri是否为空，不为空则下载（预留相关处理接口）
         * 2.uri为空，则表明数据在本地，判断数据是否为压缩格式，条件：formatCode.equals("tgz")
         *      一.如果是tgz格式则进行解压缩，同时将新的解压数据信息写入表。
         *      二.如果不是tgz格式（则为unzipped），进行本地数据读取。
         *
         */
        List<DataGranule> result = new ArrayList<>();

        for (DataGranule dataGranule : dataGranules) {
            //获取数据读取路径
            String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
            DataGranule outputDataGranule = DataGranules.converter(dataGranule, LandsatFormatCode.DIR);

            //数据解压输出路径
            String outputPath = pathConfigs.outputPath + outputDataGranule.getDataGranulePath();

            String formatCode = dataGranule.getFormatCode();
            String uri = dataGranule.getDataGranuleUri();
            /**
             *  判断其类型为TGZ的对其进行解压，其他类型不做处理。
             */
            if (formatCode.equalsIgnoreCase("TGZ")) {

                File destination = new File(outputPath);
                File source = new File(inputPath);
                Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
                try {
                    archiver.extract(source, destination);
                    dataGranuleRepository.save(outputDataGranule);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            dataGranule = outputDataGranule;
            }
            result.add(dataGranule);
        }
        return result;
    }

    /**
     *根据数据请求过滤数据信息
     * 如：前端请求 ndvi数据
     */

    public List<DataGranule> dataFilter(List<DataGranule> dataGranules){

        return null;
    }
}
