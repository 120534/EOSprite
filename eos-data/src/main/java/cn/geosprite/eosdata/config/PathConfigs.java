package cn.geosprite.eosdata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 9:51 2019-4-8
 * @ Description：None
 * @ Modified By：
 */
@Data
@ConfigurationProperties(prefix = "prefix")
@Component
public class PathConfigs {

    /**本地路径前缀
     * 如 dataGranulePath = raw/117/050/LC81170502018264LGN00.tgz
     *    inputPath = /mnt/disk1/geodata/
     * 那么数据真实路径为 dataGranulePath + inputPath
     * */
    public String inputPath;

    /**
     *如上，在写入数据时使用。
     */
    public String outputPath;

}
