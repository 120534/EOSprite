package cn.geosprite.eosdata.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@DynamicUpdate
@Accessors(chain = true)
public class Orders {

  @Id
  @GeneratedValue
  private Integer orderId;

  @Type(type = "com.vividsolutions.jts.geom.Geometry")
  @Column(name="data_spatial_range",columnDefinition="Geometry(Polygon,4326)")
  private Polygon dataSpatialRange;

  /**影像获取起始时间*/
  private java.sql.Date dataStartDate;
  /**影像获取结束时间*/
  private java.sql.Date dataEndDate;
  /**选择传感器*/
  private String dataSensorName;
  /**选择模型类型*/
  private String dataProductName;
  /**起始Path*/
  private String startPath;
  /**结束Path*/
  private String endPath;
  /**起始Row*/
  private String startRow;
  /**结束Row*/
  private String endRow;
  /**订单状态*/
  private Integer status;

  /**订单提交时间*/
  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private java.sql.Timestamp orderSubmittedTime;

  /**订单完成时间*/
  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private java.sql.Timestamp orderCompletedTime;

  public Orders() {
  }

}
