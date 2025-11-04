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
public class UserServiceCustomerRequest {

    @Valid
    @NotBlank(message = "Customer number is required")
    private String customerNumber;
}
