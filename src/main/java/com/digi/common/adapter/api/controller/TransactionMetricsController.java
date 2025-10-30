package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.TransactionMetricsService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.TransactionMetricsReq;
import com.digi.common.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class TransactionMetricsController {

    @Autowired
    private TransactionMetricsService transactionMetrics;

    @PostMapping("/txnMetrics")
    public GenericResponse<Map> getTransactionMetrics(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.getTransactionMetrics(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/txnMetricsByPlatform")
    public GenericResponse<Map> getMetricsByPlatform(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.getTransactionMetricsByPlatform(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/txnMetricsByModel")
    public GenericResponse<Map> getMetricsByModel(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.getTransactionMetricsByModel(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/txnMetricsByDateRange")
    public GenericResponse<Map> getMetricsByDateRange(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.getTransactionMetricsByDateRange(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/txnMetricsPieChart")
    public GenericResponse<Map> getMetricsPieChart(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.getMetricsPieChart(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/txnMetricsByService")
    public GenericResponse<Map> txnMetricsByService(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody TransactionMetricsReq request) {

        return transactionMetrics.txnMetricsByService(unit, channel, lang, serviceId, request);
    }
}
