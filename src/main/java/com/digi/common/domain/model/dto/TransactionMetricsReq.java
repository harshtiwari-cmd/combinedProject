package com.digi.common.domain.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TransactionMetricsReq {

	String startDate;
	String endDate;
	String type;
	Map<String, Object> filter;
}
