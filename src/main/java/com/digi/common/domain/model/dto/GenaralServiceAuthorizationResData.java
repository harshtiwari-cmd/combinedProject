package com.digi.common.domain.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenaralServiceAuthorizationResData implements Serializable {

	private static final long serialVersionUID = -2242447226993251409L;
	private String message;
	private String status;
	private String errorFlag;
	private String processID;
}
