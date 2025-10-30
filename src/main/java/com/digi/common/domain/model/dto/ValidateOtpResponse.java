package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidateOtpResponse implements Serializable{
	private static final long serialVersionUID = 1L;

	private String otpMessage;
	private String otpStatus;
	private String otp;
	
	
}
