package cn.geosprite.eosdata.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "order_data_granules")
public class OrderDataGranule {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer orderId;

    private String dataGranuleId;

    public OrderDataGranule() {
    }

    //    @OneToMany(mappedBy = "data_granule_id")
//    private List<DataGranule> dataGranules;
}
