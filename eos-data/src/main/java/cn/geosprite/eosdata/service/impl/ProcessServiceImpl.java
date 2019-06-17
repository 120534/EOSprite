package cn.geosprite.eosdata.service.impl;

import cn.geosprite.eosdata.config.PathConfigs;
import cn.geosprite.eosdata.dao.DataGranuleRepository;
import cn.geosprite.eosdata.dao.OrdersRepository;
import cn.geosprite.eosdata.dataGranuleUtils.DataGranules;
import cn.geosprite.eosdata.dto.OrderStatus;
import cn.geosprite.eosdata.entity.DataGranule;
import cn.geosprite.eosdata.entity.Orders;
import cn.geosprite.eosdata.enums.LandsatEnum;
import cn.geosprite.eosdata.enums.LandsatFormatCode;
import cn.geosprite.eosdata.enums.OrderStatusEnum;
import cn.geosprite.eosdata.service.ProcessService;
import cn.geosprite.eosprocess.service.LasrcService;
import cn.geosprite.eosprocess.service.BandMathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ Date       ：Created in 21:14 2019-4-7
 * @ Description：None
 * @ Modified By：
 * @author 17491
 */

@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {

    private PreProcessServiceImpl preProcessService;
    private BandMathService bandMathService;
    private LasrcService lasrcService;
    private DataGranuleRepository dataGranuleRepository;
    private OrdersRepository ordersRepository;
    private DataServiceImpl dataService;
    private PathConfigs pathConfigs;


    @Autowired
    public ProcessServiceImpl(PreProcessServiceImpl preProcessService, LasrcService lasrcService,
                              BandMathService bandMathService, DataGranuleRepository dataGranuleRepository,
                              PathConfigs pathConfigs, DataServiceImpl dataService,
                              OrdersRepository ordersRepository) {
        this.preProcessService = preProcessService;
        this.lasrcService = lasrcService;
        this.bandMathService = bandMathService;
        this.dataGranuleRepository = dataGranuleRepository;
        this.pathConfigs = pathConfigs;
        this.dataService = dataService;
        this.ordersRepository = ordersRepository;
    }

    @Override
    public List<DataGranule> doSR(List<DataGranule> dataGranules) {
        //返回已经做过大气校正的dataGranule信息
        List<DataGranule> result = new ArrayList<>();
        //确保所有数据都在本地且已经解压
        List<DataGranule> dirDataGranule = preProcessService.extractFiles(dataGranules);
        for (DataGranule dataGranule: dirDataGranule){
            DataGranule outputDataGranule = DataGranules.converter(dataGranule, LandsatFormatCode.SR);

            //如果是解压后的原始数据，则需要进行大气校正
            if (dataGranule.getProductCode().equalsIgnoreCase(LandsatFormatCode.DIR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(LandsatFormatCode.DIR.getFormat())){

                //大气校正的输入路径
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();

                /**
                 *
                 * /mnt/disk1/geodata/LC08/L1TP_C1_T1_SR/TIFF/113/026/2018/12/30/LC81130262018364LGN00
                 * */
                String outputPath = pathConfigs.outputPath + outputDataGranule.getDataGranulePath();

                //进行大气校正
                String logs  = lasrcService.doLasrc(inputPath, outputPath);
                log.info("atmosphere correction is done,{}", logs);

                /**
                 *
                 * 如：
                 * http://192.168.14.212/LC08/L1TP_C1_T1_NDVI/PNG/113/026/2018/12/30/LC81130262018364LGN00.PNG
                 * LC08/L1TP_C1_T1_NDVI/TIFF/126/034/2019/03/15/LC81260342019074LGN00_NDVI.tiff
                 * 需要对SR的路径进行操作，将其改变为资源发布的URL
                 * */

                String downloadURL = pathConfigs.staticResourcePrefix + outputDataGranule.getDataGranulePath();

                String previewPath = bandMathService.doTrueColorComposite(outputPath);
                String previewURL = pathConfigs.staticResourcePrefix + previewPath;

                dataGranuleRepository.save(outputDataGranule.setDataGranuleUri(downloadURL).setDataGranulePreview(previewURL));
                dataGranule = outputDataGranule;
            }
            result.add(dataGranule);
        }
        return result;
    }

    @Override
    public List<DataGranule> doNDVI( List<DataGranule> dataGranules) {
        /**
         *先要判断是否计算过Ndvi，然后在对其进行下一步操作。
         * 但是这里判断是否做过nvdi，逻辑应该放在doNdvi代码前。
         */
        List<DataGranule> result = new ArrayList<>();
        //确保数据都做过大气校正
        List<DataGranule> list = doSR(dataGranules);
        for (DataGranule dataGranule: list) {
            DataGranule ndviPng = DataGranules.converter(dataGranule, LandsatFormatCode.NDVI_PNG);
            DataGranule ndviTiff = DataGranules.converter(dataGranule, LandsatFormatCode.NDVI_TIFF);

            if (dataGranule.getProductCode().equalsIgnoreCase(LandsatFormatCode.SR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(LandsatFormatCode.SR.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                String pngPath = pathConfigs.inputPath + ndviPng.getDataGranulePath();
                String tiffPath = pathConfigs.inputPath + ndviTiff.getDataGranulePath();

                log.info("start calculating ndvi for {}", dataGranule.toString());
                //这里有点问题，getNdvi应该处理多幅影像

                bandMathService.doNDVI(inputPath, pngPath, tiffPath);
                log.info("ndvi calculation has ended");

                String tiffUri = pathConfigs.staticResourcePrefix + ndviTiff.getDataGranulePath();
                String pngPreviewUri = pathConfigs.staticResourcePrefix + ndviPng.getDataGranulePath();
                dataGranuleRepository.updateDataGranuleURIAndPreview(ndviTiff.getDataGranuleId(),tiffUri,pngPreviewUri);

                result.add(dataGranuleRepository.findDataGranuleByDataGranuleId(ndviTiff.getDataGranuleId()));
            }
                result.add(dataGranule);
        }
        return result;
    }

    @Override
    public List<DataGranule> doNDWI(List<DataGranule> dataGranules){
        List<DataGranule> result = new ArrayList<>();

        //确保数据都做过大气校正
        List<DataGranule> list = doSR(dataGranules);

        for (DataGranule dataGranule: list) {
            DataGranule ndwiPng = DataGranules.converter(dataGranule, LandsatFormatCode.NDWI_PNG);
            DataGranule ndwiTiff = DataGranules.converter(dataGranule, LandsatFormatCode.NDWI_TIFF);

            if (dataGranule.getProductCode().equalsIgnoreCase(LandsatFormatCode.SR.getProductCode()) &&
                    dataGranule.getFormatCode().equalsIgnoreCase(LandsatFormatCode.SR.getFormat())){
                String inputPath = pathConfigs.inputPath + dataGranule.getDataGranulePath();
                String pngPath = pathConfigs.inputPath + ndwiPng.getDataGranulePath();
                String tiffPath = pathConfigs.inputPath + ndwiTiff.getDataGranulePath();

                //getNdvi应该设置处理多幅影像
                bandMathService.doNDWI(inputPath, pngPath, tiffPath);


                /**
                 *这里的PNG不在单独存储到dataGranule表中了，处理完后，更新dataGranulePreview，直接给定链接。
                 * http://192.168.14.212/LC08/L1TP_C1_T1_NDVI/PNG/113/026/2018/12/30/LC81130262018364LGN00.PNG
                 * LC08/L1TP_C1_T1_NDVI/TIFF/126/034/2019/03/15/LC81260342019074LGN00_NDVI.tiff
                 */

                String tiffUri = pathConfigs.staticResourcePrefix + ndwiTiff.getDataGranulePath();
                String pngPreviewUri = pathConfigs.staticResourcePrefix + ndwiPng.getDataGranulePath();
                dataGranuleRepository.updateDataGranuleURIAndPreview(ndwiTiff.getDataGranuleId(),tiffUri,pngPreviewUri);

                result.add(dataGranuleRepository.findDataGranuleByDataGranuleId(ndwiTiff.getDataGranuleId()));
            }
            result.add(dataGranule);
        }
        return result;
    }

    /**
     * 前端发送orderId信息
     * 1.接收orderId开始处理数据，
     * 2.数据处理后，更新uri信息
     * 3.处理完后修改订单的order_complete_time，以及orderStatus
     * 4.再把数据信息返回到前端
     */
    public OrderStatus process(Integer orderId){
        /**查询得到订单的ID*/
        Orders orders = ordersRepository.findByOrderId(orderId);
        /**获取到productName，判断需要什么数据处理方法*/
        String productName = orders.getDataProductName();
        /**根据orderId查询到订单包含的DataGranule（注意：这里的dataGranule是提前写入数据库的，还没真正处理过）*/
        List<DataGranule> dataGranules = dataService.findDataGranulesByOrderId(orderId);

        /**创建两个集合，如果数据库已经由处理过的数据时，放入product中，就不用处理了，否则放入raw，进行数据处理*/
        List<DataGranule> product = new ArrayList<>();
        List<DataGranule> raw = new ArrayList<>();

        /**判断dataGranule是否已经全部处理过(条件是URL都不为空)，那么就可以直接返回*/
        long a = 0;
        long b = dataGranules.stream().filter(x -> x.getDataGranuleUri()==null && x.getDataGranulePreview()==null).count();

        /**遍历订单里面所有的dataGranule，依据URI是否为空，分别分入两个集合*/

        for (DataGranule dataGranule: dataGranules){
            if (dataGranule.getDataGranuleUri() == null){
                raw.add(DataGranules.converterBack(dataGranule, LandsatFormatCode.TGZ));
            }else {
                product.add(dataGranule);
            }
        }

        // TODO:测试判断条件，在数据全部为RAW的时候，数据处理完返回的状态为取消,
        //  抽象一个模式匹配的方法，实现方法名可扩展，同时添加SR的计算。
        /**如果都处理过则不用进行处理，直接返回结果*/
        if (raw.size() != 0){
            /**创建一个集合，接受处理后的数据*/
            List<DataGranule> list;
            //根据productName进行判断，调用哪个方法处理数据 case
            if (productName.equalsIgnoreCase(LandsatFormatCode.NDVI_TIFF.getProductCode())){
                list = doNDVI(raw);
            }else if (productName.equalsIgnoreCase(LandsatFormatCode.NDWI_TIFF.getProductCode())){
                list = doNDWI(raw);
            }else {
                throw new RuntimeException("no method existing for " + productName);
            }

            /**
             * 根据订单提交的计算模型，匹配对应的计算方法
             * 目前考虑直接使用模式匹配
             * 1. 读取order里面的信息，拿到order.
             * TODO 这里需要考虑使用哪个Enum，或许使用LandsatEnum更合适
             * */

            LandsatEnum code = LandsatEnum.fromCode(productName);

            assert code != null;
            switch (code){
                case LANDSAT8_SR:
                    list = doSR(raw);
                    break;
                case LANDSAT8_NDVI:
                    list = doNDVI(raw);
                    break;
                case LANDSAT8_NDWI:
                    list = doNDWI(raw);
                    break;
                case LANDSAT8_KMEANS:
//                   list = doKMEANS();
                    break;
                case LANDSAT8_DDI:
//                   list = doDDI();
                    break;
                default:
                    throw new IllegalStateException("Unexpected productName: " + productName);
            }

            /**把处理后的dataGranule信息并入product集合中，这个集合包含了全部处理过的数据信息*/
            list.addAll(product);

            /**异常处理，后期再修改把，这里只是判断了URI是否为空，将其分为处理遇到异常，或者处理完成*/

            /**list3包含处理失败的数据*/
            List<DataGranule>  list3 = list.stream().filter(x -> x.getDataGranuleUri() == null).collect(Collectors.toList());

            /**list4包含处理成功的数据*/
            //List<DataGranule>  list4 = list.stream().filter(x -> x.getDataGranuleUri()!= null).collect(Collectors.toList());

            /**添加时间戳到Order表中，同时返回到前端*/
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            /**更新订单信息，加入订单完成时间和订单状态*/
            ordersRepository.updateOrderFinish(orderId,timestamp, OrderStatusEnum.FINISHED.getCode());

            OrderStatus orderStatus ;
            if (list3.size()!=0){
                orderStatus = new OrderStatus()
                        .setMessage(OrderStatusEnum.ERROR.getMessage())
                        .setDataGranuleList(list3)
                        .setOrderCompletedTime(timestamp);
            }else {
                orderStatus = new OrderStatus()
                        .setMessage(OrderStatusEnum.FINISHED.getMessage())
                        .setOrderCompletedTime(timestamp);
            }
            return orderStatus;
        }else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            /**更新订单信息，加入订单完成时间和订单状态*/
            ordersRepository.updateOrderFinish(orderId,timestamp, OrderStatusEnum.FINISHED.getCode());
            return new OrderStatus().setOrderCompletedTime(timestamp)
                    .setMessage(OrderStatusEnum.FINISHED.getMessage());
        }


    }
}
