package com.digi.common.domain.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecialRateResData implements Serializable {

	private static final long serialVersionUID = 15572275630611275L;
	private String message;
	private String status;
//	private String errorFlag;
//	private String requestReferenceNumber;
//	private String transactionReferenceNo;
	private String processID;

}
