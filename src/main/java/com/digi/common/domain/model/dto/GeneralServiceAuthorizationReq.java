package com.digi.common.domain.model.dto;

import com.digi.common.dto.DeviceInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeneralServiceAuthorizationReq implements Serializable {
	
	private static final long serialVersionUID = 2777683576651329235L;
	private DeviceInfoDto deviceInfo;
	private String service_id;

}
