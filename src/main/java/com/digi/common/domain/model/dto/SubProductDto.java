package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubProductDto {

    private Long subProductId;
    private String subProductName;
    private String subProductCategory;
    private String description;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    }
