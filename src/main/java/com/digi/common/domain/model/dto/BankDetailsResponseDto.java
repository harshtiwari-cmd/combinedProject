package com.digi.common.domain.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankDetailsResponseDto {

    @Email(message = "email should be valid")
    private String mail;

    @NotNull(message = "contact should not be null")
    private Long contact;

    @NotNull(message = "internationalContact should not be null")
    private String internationalContact;

    private List<SocialMedia> followUs;


}
