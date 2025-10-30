package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationRequest {

    @Valid
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @Valid
    @NotBlank(message = "Card pin is required")
    @Pattern(regexp = "\\d{4}", message = "Card pin must be exactly 4 digits")
    private String pin;
}
