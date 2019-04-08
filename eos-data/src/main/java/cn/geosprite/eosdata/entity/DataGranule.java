package cn.geosprite.eosdata.entity;


import javax.persistence.*;
import java.sql.Date;

@Entity
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

  public String getDataGranuleId() {
    return dataGranuleId;
  }

  public void setDataGranuleId(String dataGranuleId) {
    this.dataGranuleId = dataGranuleId;
  }

  public String getSensorCode() {
    return sensorCode;
  }

  public void setSensorCode(String sensorCode) {
    this.sensorCode = sensorCode;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getTileCode() {
    return tileCode;
  }

  public void setTileCode(String tileCode) {
    this.tileCode = tileCode;
  }

  public Date getSceneDate() {
    return sceneDate;
  }

  public void setSceneDate(Date sceneDate) {
    this.sceneDate = sceneDate;
  }

  public String getFormatCode() {
    return formatCode;
  }

  public void setFormatCode(String formatCode) {
    this.formatCode = formatCode;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public String getDataGranulePath() {
    return dataGranulePath;
  }

  public void setDataGranulePath(String dataGranulePath) {
    this.dataGranulePath = dataGranulePath;
  }

  public String getDataGranuleUri() {
    return dataGranuleUri;
  }

  public void setDataGranuleUri(String dataGranuleUri) {
    this.dataGranuleUri = dataGranuleUri;
  }

  @Override
  public String toString() {
    return "DataGranule{" +
            "dataGranuleId='" + dataGranuleId + '\'' +
            ", sensorCode='" + sensorCode + '\'' +
            ", productCode='" + productCode + '\'' +
            ", tileCode='" + tileCode + '\'' +
            ", sceneDate=" + sceneDate +
            ", formatCode='" + formatCode + '\'' +
            ", dataSource='" + dataSource + '\'' +
            ", dataGranulePath='" + dataGranulePath + '\'' +
            ", dataGranuleUri='" + dataGranuleUri + '\'' +
            '}';
  }
}
