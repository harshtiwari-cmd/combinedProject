package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.TransactionMetricsReq;
import com.digi.common.dto.GenericResponse;

import java.util.Map;

public interface TransactionMetricsService {

    GenericResponse<Map> getTransactionMetrics(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

    GenericResponse<Map> getTransactionMetricsByPlatform(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

    GenericResponse<Map> getTransactionMetricsByModel(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

    GenericResponse<Map> getTransactionMetricsByDateRange(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

    GenericResponse<Map> getMetricsPieChart(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

    GenericResponse<Map> txnMetricsByService(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request);

}
