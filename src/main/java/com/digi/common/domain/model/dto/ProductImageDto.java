package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long productId;
    private String productImage;
    private List<SubProductImageDto> subProductImages;

}
