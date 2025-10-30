package com.digi.common.adapter.repository;

import com.digi.common.infrastructure.persistance.RbxTLocatorNewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RbxTLocatorNewRepository extends JpaRepository<RbxTLocatorNewEntity, String> {

    @Query("SELECT r FROM RbxTLocatorNewEntity r WHERE LOWER(r.locatorType) = LOWER(:locatorType) AND r.isActive = 'Y'")
    List<RbxTLocatorNewEntity> findByLocatorTypeIgnoreCase(@Param("locatorType") String locatorType);

}