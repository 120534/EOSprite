package cn.geosprite.eosdata.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "data_granules")
public class DataGranule {

  @Id
  private String dataGranuleId;

  private String sensorCode;

  private String productCode;

  private String tileCode;

  private Date sceneDate;

  private String formatCode;

  private String dataSource;

  private String dataGranulePath;

  private String dataGranuleUri;

  public DataGranule(String dataGranuleId, String sensorCode, String productCode, String tileCode, Date sceneDate, String formatCode, String dataSource, String dataGranulePath, String dataGranuleUri) {
    this.dataGranuleId = dataGranuleId;
    this.sensorCode = sensorCode;
    this.productCode = productCode;
    this.tileCode = tileCode;
    this.sceneDate = sceneDate;
    this.formatCode = formatCode;
    this.dataSource = dataSource;
    this.dataGranulePath = dataGranulePath;
    this.dataGranuleUri = dataGranuleUri;
  }

  public DataGranule() {
  }
}
