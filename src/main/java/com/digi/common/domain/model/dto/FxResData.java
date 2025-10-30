package com.digi.common.domain.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FxResData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String currencyCode;
	private String isoCurrencyCode;
	private String desEng;
	private String desNationaLanguage;
	private String altCurrencyCode;
	private String buyRate;
	private String sellRate;
	private String eRemittanceRate;

}
