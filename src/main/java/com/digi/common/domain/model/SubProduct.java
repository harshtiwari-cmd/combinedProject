package com.digi.common.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sub_products")
public class SubProduct extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductType productType;

    // Existing columns (English)
    @Column(name = "sub_product_name", nullable = false)
    private String name;

    @Column(name = "sub_product_category")
    private String category;


    // New columns for Arabic
    @Column(name = "sub_product_name_ar")
    private String nameAr;

    @Column(name = "sub_product_category_ar")
    private String categoryAr;


    @Lob
    @Column(name = "sub_product_image")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] image;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

   }
