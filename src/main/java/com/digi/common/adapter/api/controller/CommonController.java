package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.*;
import com.digi.common.domain.model.dto.*;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.LabelListRes;
import com.digi.common.dto.ProductMasterDto;
import com.digi.common.dto.TermsAndConditionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private I18Service i18Service;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private TermAndConditionService termService;

    @Autowired
    private GeneralService generalService;

    @Autowired
    private CustomizerService customizerService;

    @PostMapping("/labels")
    public GenericResponse<LabelListRes> labelList(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL)String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE)String lang) {

        return i18Service.labelList(unit, channel, lang);
    }

    @PostMapping("/fxRate")
    public GenericResponse<List<FxResData>> getFxRate(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = false) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = false) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = false) String screenId,
            @RequestBody ExRateRequest request) {

        serviceId = "EXRATE";
        log.info("getFxRate-request {} :",request);
        return exchangeRateService.getfxRate(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }

    @PostMapping("/specialRate")
    public GenericResponse<SpecialRateResData> getSpecialRate(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestBody SpecialRateRequest request) {

        return exchangeRateService.getSpecialRate(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }

    @PostMapping("/menus")
    public GenericResponse<List<ProductMasterDto>> getMenus(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestBody(required = false) Map<String,Object> request) {

        return menuService.getMenus(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }

    @PostMapping("/termsandcondition")
    public GenericResponse<List<TermsAndConditionDto>> getTermsAndCondition(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = false) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = false) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = false) String screenId) {

        return termService.getTermsAndConditions(unit, channel, lang, serviceId, screenId);
    }

    @PostMapping("/general-authorization")
    public GenericResponse<GenaralServiceAuthorizationResData> getGeneralServiceAuthorization(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestBody GeneralServiceAuthorizationReq request) {

        return generalService.getGeneralServiceAuthorization(unit, channel, lang, serviceId, request);
    }

    @PostMapping("/customizer")
    public GenericResponse<?> customizer(
            @RequestHeader(name = AppConstants.UNIT, required = false) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = false) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestBody(required = false) Map<String,Object> request) {

        return customizerService.customizer(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }

    @PostMapping("/serviceEntitlement")
    public GenericResponse<Map<String, Object>> serviceEntitlement(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId) {

        return menuService.serviceEntitlement(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
    }
}