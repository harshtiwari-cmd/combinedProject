package com.digi.common.domain.model.dto;

import com.digi.common.dto.DeviceInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateOtpRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String otpValue;
	private DeviceInfoDto deviceInfo;

}
