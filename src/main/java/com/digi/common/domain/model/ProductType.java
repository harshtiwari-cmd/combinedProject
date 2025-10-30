package com.digi.common.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_types")
public class ProductType extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    // Existing columns (English)
    @Column(name = "product_name", nullable = false, unique = true)
    private String name;

    @Column(name = "product_category", nullable = false)
    private String productCategory;

    // New columns for Arabic
    @Column(name = "product_name_ar")
    private String nameAr;

    @Column(name = "product_category_ar")
    private String categoryAr;

    @Lob
    @Column(name = "product_image")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] image;


    @OneToMany(mappedBy = "productType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubProduct> subProducts;



  }
