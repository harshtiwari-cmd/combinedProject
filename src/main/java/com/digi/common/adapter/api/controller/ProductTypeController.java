package com.digi.common.adapter.api.controller;


import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.ProductImageDto;
import com.digi.common.domain.model.dto.ProductTypeDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.HeaderDeviceConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    // ------------------------------------------------------------------------
    // Retrieve Eligible Products
    // ------------------------------------------------------------------------
    @PostMapping("/retrieve-eligible-products")
    public GenericResponse<List<ProductTypeDto>> getActiveProducts(
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage,
            @RequestBody RequestDto requestDto) {

        List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
        if (!missingHeaders.isEmpty()) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC), null);
        }

        if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC), null);
        }

        String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);
        DefaultHeadersDto headers = new DefaultHeadersDto(serviceId, moduleId, subModuleId, screenId, channel, languageCode);


        GenericResponse<List<ProductTypeDto>> response = productTypeService.getActiveProductTypes(headers, requestDto);

        return response;
    }


    // ------------------------------------------------------------------------
    // Retrieve All Product Images
    // ------------------------------------------------------------------------
    @PostMapping("/retrieve-all-images")
    public GenericResponse<List<ProductImageDto>> getActiveProductImages(
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage,
            @RequestBody RequestDto requestDto) {

        List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
        if (!missingHeaders.isEmpty()) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC), null);
        }

        if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC), null);
        }

        String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);
        DefaultHeadersDto headers = new DefaultHeadersDto(serviceId, moduleId, subModuleId, screenId, channel, languageCode);

        GenericResponse<List<ProductImageDto>> response = productTypeService.getActiveProductImages(headers, requestDto);

        return response;
    }


    // ------------------------------------------------------------------------
    // Retrieve Product/SubProduct Image By ID
    // ------------------------------------------------------------------------
    @PostMapping("/retrive-image")
    public GenericResponse<Map<String, String>> getProdOrSubProdImagesById(
            @RequestBody RequestDto requestDto,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
        if (!missingHeaders.isEmpty()) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC), null);
        }

        if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC), null);
        }

        if (HeaderDeviceConstant.checkRequestInfoDto(requestDto.getRequestInfoDto())) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.REQUEST_INFO_DESC), null);
        }

        String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);
        DefaultHeadersDto headers = new DefaultHeadersDto(serviceId, moduleId, subModuleId, screenId, channel, languageCode);

        GenericResponse<Map<String, String>> response = productTypeService.getProdOrSubProdImgsById(
                requestDto.getRequestInfoDto().getProductType(),
                requestDto.getRequestInfoDto().getId(),
                requestDto,
                headers);


        return response;
    }


    // ------------------------------------------------------------------------
    // Get Sub Product Images By Product ID
    // ------------------------------------------------------------------------
    @PostMapping("/getSubImgsByProdId")
    public GenericResponse<List<Map<String, String>>> getSubProdImgsByProdId(
            @RequestBody RequestDto requestDto,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
        if (!missingHeaders.isEmpty()) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC), null);
        }

        if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC), null);
        }

        if (HeaderDeviceConstant.checkRequestInfoDto(requestDto.getRequestInfoDto())) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.REQUEST_INFO_DESC), null);
        }

        String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);
        DefaultHeadersDto headers = new DefaultHeadersDto(serviceId, moduleId, subModuleId, screenId, channel, languageCode);


        GenericResponse<List<Map<String, String>>> response = productTypeService.getSubProdImgsByPrdId(
                requestDto.getRequestInfoDto().getId(),
                requestDto,
                headers);


        return response;
    }


    // ------------------------------------------------------------------------
    // Get Sub Product Images With Description By Product ID
    // ------------------------------------------------------------------------
    @PostMapping("/getSubProdImgWithDesc")
    public GenericResponse<List<Map<String, String>>> getSubProdImgWithDescByProdId(
            @RequestBody RequestDto requestDto,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
        if (!missingHeaders.isEmpty()) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC), null);
        }

        if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC), null);
        }

        if (HeaderDeviceConstant.checkRequestInfoDto(requestDto.getRequestInfoDto())) {
            return new GenericResponse<>(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.REQUEST_INFO_DESC), null);
        }

        String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);
        DefaultHeadersDto headers = new DefaultHeadersDto(serviceId, moduleId, subModuleId, screenId, channel, languageCode);

        GenericResponse<List<Map<String, String>>> response = productTypeService.getSubProdImgsWithDescByProdId(
                requestDto.getRequestInfoDto().getId(),
                requestDto,
                headers);

        return response;
    }


}