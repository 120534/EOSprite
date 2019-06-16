package cn.geosprite.eosdata.dto;

import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.enums.LandsatEnum;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.experimental.Accessors;
import monocle.std.list;
import monocle.std.option;
import org.springframework.beans.BeanUtils;

import java.sql.Date;
import java.util.List;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 17:29 2019-4-25
 * @ Description：用于前端弹窗显示order Detail
 * @ Modified By：
 */
@Data
@Accessors(chain = true)
public class DataGranuleOutputDTO {
    //	sensorCode	productCode	tileCode	sceneDate	formatCode	dataSource	option
    /**
     * Landsat-8,MODIS, Sentinel-2 ,etc.
     */
    private String dataGranuleId;

    private String dataSensorName;
    /**
     * the name of product , like 归一化植被指数
     */
    private String dataProductName;
    /**
     * raw data Identifier
     */
    private String identifier;
    /**
     * 行列号 如 117/025
     */
    private String tileCode;
    /**
     * 数据获取日期
     */
    private Date sceneDate;
    /**
     * TIFF , TGZ , PNG ,etc..
     */
    private String formatCode;
    /**
     * USGS, GOOGLE CLOUD, AWS ,etc
     */
    private String dataSource;
    /**
     * 数据下载链接
     */
    private String dataGranuleUri;

    /**预览图*/
    private String dataGranulePreview;

    public static DataGranuleOutputDTO converToDataGranuleOutputDTO(DataGranule dataGranule){
        DataGranuleDTOConvert dataGranuleDTOConvert = new DataGranuleDTOConvert();
        return dataGranuleDTOConvert.doBackward(dataGranule);
    }

    private static class DataGranuleDTOConvert implements DTOConvert<DataGranuleOutputDTO, DataGranule> {

        @Override
        public DataGranule doForward(DataGranuleOutputDTO dataGranuleOutputDTO) {
            throw new AssertionError("DataGranuleOutputDTO.doForward is not available");
        }

        @Override
        public DataGranuleOutputDTO doBackward(DataGranule dataGranule) {
            DataGranuleOutputDTO dataGranuleOutputDTO = new DataGranuleOutputDTO();
            /**
             * dataGranule 可以直接使用的信息包括:
             * 1.dataSource
             * 2.formatCode
             * 3.sceneDate
             * 4.tileCode
             * 5.dataGranuleUri
             */

            BeanUtils.copyProperties(dataGranule, dataGranuleOutputDTO);
            /**
             * 需要修改的属性包括：
             * 1.identifier 取'/'分割最后的字符即为影像的标识。
             * 2.dataSensorName
             * 3.dataProductName
             */
            String path = dataGranule.getDataGranulePath();
            String identifier;
            if (path != null && path.length() != 0) {
                List<String> list = Splitter.on("/").splitToList(path);
                identifier = list.get(list.size() - 1);
            } else {
                throw new RuntimeException("dataGranule path is null or path's length is equal to zero");
            }
            //TODO: CHECK THE SENSOR IS LANDSAT OR NOT.
            /**  code to name */
            String sensorName = LandsatEnum.fromCode(dataGranule.getSensorCode()).getName();
            String productName = LandsatEnum.fromCode(dataGranule.getProductCode()).getName();
            dataGranuleOutputDTO
                    .setIdentifier(identifier)
                    .setDataProductName(productName)
                    .setDataSensorName(sensorName);
            return dataGranuleOutputDTO;
        }
    }
}
