package com.digi.common.domain.model.dto;

import com.digi.common.dto.DeviceInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExRateRequest implements Serializable{
	 
	private static final long serialVersionUID = -3269917440859200500L;
	
	private DeviceInfoDto deviceInfo;

}
