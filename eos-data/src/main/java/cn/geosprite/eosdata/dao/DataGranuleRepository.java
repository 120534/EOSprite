package cn.geosprite.eosdata.dao;

import cn.geosprite.eosdata.entity.DataGranule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface DataGranuleRepository extends JpaRepository<DataGranule, String> {

    DataGranule findDataGranuleByDataGranuleId(String dataGranuleId);

    List<DataGranule> findDataGranulesByDataGranuleId(List<String> dataGranuleIds);

}

