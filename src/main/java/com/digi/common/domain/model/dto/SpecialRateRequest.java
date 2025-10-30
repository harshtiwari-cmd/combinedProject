package com.digi.common.domain.model.dto;

import com.digi.common.dto.DeviceInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpecialRateRequest implements Serializable{

	private static final long serialVersionUID = 7297236916374601019L;
	private DeviceInfoDto deviceInfo;
	private String currency;
	private String amount;
	private String tenure;
	private String narration;
	private String date;
	private String flag;

}
