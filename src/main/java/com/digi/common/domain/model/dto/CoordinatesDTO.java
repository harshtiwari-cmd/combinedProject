package com.digi.common.domain.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoordinatesDTO {
    private double latitude;
    private double longitude;
}
