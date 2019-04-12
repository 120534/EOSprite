package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.service.PreProcessService;
import org.apache.commons.beanutils.BeanUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 16:37 2019-4-12
 * @ Description：None
 * @ Modified By：
 */
@Service
public class PreProcessServiceImpl implements PreProcessService {

    private DataGranuleRepository dataGranuleRepository;

    @Autowired
    public PreProcessServiceImpl(DataGranuleRepository dataGranuleRepository) {
        this.dataGranuleRepository = dataGranuleRepository;
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
            //获取dataGranul里面可能需要修改的信息
            String inputPath = dataGranule.getDataGranulePath();
            String outputPath = dataGranule.getDataGranulePath() + "_DIR";
            String dataGranuleId = outputPath;
            String formatCode = dataGranule.getFormatCode();
            String uri = dataGranule.getDataGranuleUri();

            DataGranule outputDataGranule = new DataGranule();

            if (uri != null) {
                //后期补充
                DataGranule dataGranule1 = downloadData(dataGranule);
            }

            if (!formatCode.equalsIgnoreCase("DIR")) {

                if (formatCode.equalsIgnoreCase("TGZ")) {

                    File destination = new File(outputPath);
                    File source = new File(inputPath);
                    Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
                    try {
                        archiver.extract(source, destination);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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

            result.add(outputDataGranule);
        }

        return result;
    }
}
