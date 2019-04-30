package cn.geosprite.eosdata.dto;

import cats.kernel.instances.order;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 12:04 2019-4-30
 * @ Description：None
 * @ Modified By：
 * @author 17491
 */
@Data
@Accessors(chain = true)
public class PollingDTO {

    private String dataGranuleId;
    /**
     * 数据下载链接
     */
    private String dataGranuleUri;

    /**预览图*/
    private String dataGranulePreview;

}
