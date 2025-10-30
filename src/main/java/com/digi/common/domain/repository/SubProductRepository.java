package com.digi.common.domain.repository;

import com.digi.common.domain.model.SubProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubProductRepository extends JpaRepository<SubProduct, Long>{


    @Query("select sbp.image from SubProduct sbp where sbp.id=:id and sbp.active=true")
    byte[] findSubProductImageById(@Param("id") Long id);



}