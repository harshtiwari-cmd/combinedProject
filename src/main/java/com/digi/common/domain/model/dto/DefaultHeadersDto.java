package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultHeadersDto {
    private String serviceId;
    private String moduleId;
    private String subModuleId;
    private String screenId;
    private String channel;
    private String acceptLanguage;
}
