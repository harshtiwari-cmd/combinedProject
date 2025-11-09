package com.digi.common.domain.model;

import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.BankMiddlewareRequest;
import com.digi.common.domain.model.dto.BankMiddlewareResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "BankMiddlewareClient", url = "${bank.middleware.url}")
public interface BankMiddlewareClient {

    @PostMapping("/api/v1/bank-middleware")
    public ResponseEntity<BankMiddlewareResponse> getBankResponse(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstants.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestBody BankMiddlewareRequest request
            );
}
