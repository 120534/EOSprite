package cn.geosprite.eosdata.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Data
@Table(name = "order_data_granules")
@Accessors(chain = true)
public class OrderDataGranule {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer orderId;

    @OneToOne
    @JoinColumn(name = "data_granule_id")
    private DataGranule dataGranule;

    public OrderDataGranule() {
    }
}
