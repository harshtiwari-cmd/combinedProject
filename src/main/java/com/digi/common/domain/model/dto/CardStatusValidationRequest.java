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
public class CardStatusValidationRequest {

	@Valid
	@NotBlank(message = "Card number is required")
	@Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
	private String cardNumber;
}


