package com.digi.common.domain.repository;

import com.digi.common.entity.I18N;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaI18Repository extends JpaRepository<I18N, Integer>{

	@Query(value = "SELECT * from OCS_T_I18N lbl"
			+ " where lbl.unit_id=:unitId and lbl.channel_id=:channelId AND lbl.LANG_CODE=:lang AND lbl.STATUS<>'DEL' ORDER BY date_modified DESC",nativeQuery=true)
	List<I18N> getLabelList(@Param("unitId") String unitId,@Param("channelId") String channelId,@Param("lang") String lang);
}
