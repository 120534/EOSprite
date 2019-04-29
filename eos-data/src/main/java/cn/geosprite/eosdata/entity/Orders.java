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

  private java.sql.Date dataStartDate;
  private java.sql.Date dataEndDate;
  private String dataSensorName;
  private String dataProductName;

  private String startPath;
  private String endPath;
  private String startRow;
  private String endRow;
  private Integer status;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private java.sql.Timestamp orderSubmittedTime;

  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private java.sql.Timestamp orderCompletedTime;

  public Orders() {
  }

}
