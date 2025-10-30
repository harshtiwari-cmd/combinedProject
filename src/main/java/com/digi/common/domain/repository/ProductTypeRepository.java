package com.digi.common.domain.repository;

import com.digi.common.domain.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    @Query("select distinct pt from ProductType pt " +
            "left join fetch pt.subProducts sp " +
            "where pt.active = true " +
            "and (sp is null or sp.active = true)")
    List<ProductType> findAllActiveWithSubProducts();


    @Query("select pt.image from ProductType pt where pt.id=:id and pt.active=true")
    byte[] findProductImageById(@Param("id")  Long id);




    @Query("SELECT s.image " +
            "FROM SubProduct s " +
            "WHERE s.productType.id = :productId " +
            "AND s.productType.active = true")
    List<byte[]> findSubProductImagesByProductId(@Param("productId") Long productId);





    @Query("SELECT s.image, s.description, s.descriptionAr " +
            "FROM SubProduct s " +
            "WHERE s.productType.id = :productId " +
            "AND s.productType.active = true")
    List<Object[]> findSubProductImageAndDescriptionsByProductId(@Param("productId") Long productId);


}
