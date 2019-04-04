package cn.geosprite.eosdata.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "order_data_granules")
public class OrderDataGranule {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "data_granule_id", nullable = false)
    private String dataGranuleId;

    public Integer getId() {
        return id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getDataGranuleId() {
        return dataGranuleId;
    }

    public void setDataGranuleId(String dataGranuleId) {
        this.dataGranuleId = dataGranuleId;
    }

    //    @OneToMany(mappedBy = "data_granule_id")
//    private List<DataGranule> dataGranules;
}
