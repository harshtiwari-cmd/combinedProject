package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDto {

    private Long productId;
    private String productName;
    private String productCategory;
    private String productImage;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<SubProductDto> subProducts;

    }
