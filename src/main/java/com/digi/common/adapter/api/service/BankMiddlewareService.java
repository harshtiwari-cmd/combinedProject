package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.BankMiddlewareRequest;
import com.digi.common.domain.model.dto.BankMiddlewareResponse;

public interface BankMiddlewareService {
    BankMiddlewareResponse callBankMiddleware(String unit, String channel, String acceptLanguage,
                                              String serviceId, String screenId, String moduleId,
                                              String subModuleId, BankMiddlewareRequest request);
}
