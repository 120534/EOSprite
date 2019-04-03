package cn.geosprite.eosdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "data_granules")
public class DataGranule {

  @Id
  @Column(name = "data_granule_id",nullable = false)
  private String dataGranuleId;

  @Column(name = "sensor_code",nullable = false)
  private String sensorCode;

  @Column(name = "product_code",nullable = false)
  private String productCode;

  @Column(name = "tile_code",nullable = false)
  private String tileCode;

  @Column(name = "scene_date",nullable = false)
  private Date sceneDate;

  @Column(name = "format_code",nullable = false)
  private String formatCode;

  @Column(name = "data_source",nullable = false)
  private String dataSource;

  @Column(name = "data_granule_path")
  private String dataGranulePath;

  @Column(name = "data_granule_uri")
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

}
