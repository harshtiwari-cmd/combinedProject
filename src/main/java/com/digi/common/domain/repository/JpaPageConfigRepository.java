package com.digi.common.domain.repository;

import com.digi.common.entity.PageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaPageConfigRepository extends JpaRepository<PageConfig, Integer>{

	@Query(value = "SELECT * from OCS_T_SCREEN_CONFIG config"
			+ " where config.UNIT_ID=:unitId and config.CHANNEL_ID=:channelId AND config.STATUS<>'DEL' ORDER BY DATE_MODIFIED DESC",nativeQuery=true)
	List<PageConfig> getValidations(@Param("unitId") String unitId,@Param("channelId") String channelId);
}
