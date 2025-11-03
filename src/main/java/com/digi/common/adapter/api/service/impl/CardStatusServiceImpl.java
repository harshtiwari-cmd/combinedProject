package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.BankMiddlewareService;
import com.digi.common.adapter.api.service.CardStatusService;
import com.digi.common.domain.model.dto.BankMiddlewareRequest;
import com.digi.common.domain.model.dto.BankMiddlewareResponse;
import com.digi.common.domain.model.dto.CardStatusResponse;
import com.digi.common.domain.model.dto.CardStatusValidationRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.helper.CardBasicValidations;
import com.digi.common.infrastructure.persistance.CardBinMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CardStatusServiceImpl implements CardStatusService {

	private static final Logger logger = LoggerFactory.getLogger(CardStatusServiceImpl.class);

	@Autowired
	private CardBasicValidations cardBasicValidations;

	@Autowired
	private BankMiddlewareService bankMiddlewareService;

	@Override
	public GenericResponse<CardStatusResponse> validateCardStatus(String unit, String channel, String acceptLanguage, String serviceId, String screenId, String moduleId, String subModuleId, CardStatusValidationRequest request, DeviceInfo deviceInfo) {
		try {
			String cardNumber = request.getCardNumber();

			CardBinMaster matchedBin = cardBasicValidations.findMatchingBin(cardNumber);
			if (matchedBin == null) {
				logger.warn("BIN pre-check failed: no record found for cardNumber: {}", cardNumber);
				return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
			}

			if (!"ACTIVE".equalsIgnoreCase(matchedBin.getStatus())) {
				logger.warn("BIN pre-check failed: BIN not ACTIVE. bin={}, status={}", matchedBin.getBin(), matchedBin.getStatus());
				return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
			}

			if (matchedBin.getCardType() == null || !("DEBIT".equalsIgnoreCase(matchedBin.getCardType()) || "PREPAID".equalsIgnoreCase(matchedBin.getCardType()))) {
				logger.warn("BIN pre-check failed: cardType not allowed. bin={}, cardType={}", matchedBin.getBin(), matchedBin.getCardType());
				return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
			}

			BankMiddlewareRequest mwRequest = BankMiddlewareRequest.builder()
					.serviceName("PCS.CARD.INFO")
					.parameters(Collections.singletonList(BankMiddlewareRequest.Parameter.builder()
							.fieldName(AppConstant.CARD_NUMBER)
							.fieldValue(cardNumber)
							.build()))
					.build();

			BankMiddlewareResponse mwResponse = bankMiddlewareService.callBankMiddleware(
					unit != null ? unit : AppConstant.DEFAULT_UNIT,
					channel != null ? channel : AppConstant.DEFAULT_CHANNEL,
					acceptLanguage != null ? acceptLanguage : AppConstant.DEFAULT_LANGUAGE,
					serviceId != null ? serviceId : AppConstant.DEFAULT_SERVICEID,
					screenId != null ? screenId : AppConstant.DEFAULT_SCREENID,
					moduleId != null ? moduleId : AppConstant.DEFAULT_MODULEID,
					subModuleId != null ? subModuleId : AppConstant.DEFAULT_SUNMODULEID,
					mwRequest
			);

			String returnCode = mwResponse != null && mwResponse.getBankResponse() != null && mwResponse.getBankResponse().getReturnStatus() != null
					? mwResponse.getBankResponse().getReturnStatus().getReturnCode()
					: null;

			if (AppConstant.MIDDLEWARE_SUCCESS_CODE.equals(returnCode)) {
				CardStatusResponse data = CardStatusResponse.builder()
						.cardValid(true)
						.activationFlag("Active")
						.build();
				return GenericResponse.success(data);
			}

			if ("000424".equals(returnCode)) {
                return GenericResponse.error(AppConstant.INNER_SERVICE, AppConstant.INNER_SERVICE_MSG);

            }

			return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
		} catch (Exception e) {
			logger.error("Error while validating card status: {}", e.getMessage(), e);
			return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
		}
	}
}


