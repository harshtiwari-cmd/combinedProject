package com.digi.common.domain.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsRequestDto {

    @NotNull(message = "name should not be null")
    private String nameEn;
    private String nameAr;

    @Email(message = "email should not be empty")
    private String mail;
    @NotNull(message = "contact should not be null")
    private Long contact;
    @NotNull(message = "international number should not be null")
    private String internationalContact;

    private String urlEn;
    private String urlAr;
    private String displayImage;
    private Integer displayOrder;

    private List<FollowUsItemDto> followUs;
}


