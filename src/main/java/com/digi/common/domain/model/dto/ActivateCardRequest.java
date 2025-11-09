package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateCardRequest {

    @NotBlank(message = "Card Number is required")
    private String cardNumber;

    @NotBlank(message = "Customer Number is required")
    private String customerNumber;

    @Valid
    @NotBlank(message = "Card pin is required")
    @Pattern(regexp = "\\d{4}", message = "Card pin must be exactly 4 digits")
    private String newPinBlock;


}
