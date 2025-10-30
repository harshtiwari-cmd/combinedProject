package com.digi.common.adapter.api.controller;


import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.ProductImageDto;
import com.digi.common.domain.model.dto.ProductTypeDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    @PostMapping("/retrieve-eligible-products")
    public GenericResponse<List<ProductTypeDto>> getActiveProducts(
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage,
            @RequestBody RequestDto requestDto) {

        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );

        return productTypeService.getActiveProductTypes(headers,requestDto);
    }

    @PostMapping("/retrieve-all-images")
    public GenericResponse<List<ProductImageDto>> getActiveProductImages(
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage,
            @RequestBody RequestDto requestDto) {

        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );
        return productTypeService.getActiveProductImages(headers,requestDto);
    }


    @PostMapping("/retrive-image")
    public GenericResponse<Map<String,String>> getProdOrSubProdImagesById(
            @RequestBody RequestDto requestDto,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage
            ) {

        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );

        return productTypeService.getProdOrSubProdImgsById(requestDto.getRequestInfoDto().getProductType(), requestDto.getRequestInfoDto().getId(),requestDto,headers);
    }


    @PostMapping("/getSubImgsByProdId")
    public GenericResponse<List<Map<String, String>>> getSubProdImgsByProdId(@RequestBody RequestDto requestDto,
                                                                             @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
                                                                             @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
                                                                             @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
                                                                             @RequestHeader(name = AppConstants.SCREENID) String screenId,
                                                                             @RequestHeader(name = AppConstants.CHANNEL) String channel,
                                                                             @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage
    ) {

        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );
        GenericResponse<List<Map<String, String>>> subProdImgsByPrdId = productTypeService.getSubProdImgsByPrdId(requestDto.getRequestInfoDto().getId(),requestDto, headers);

        return subProdImgsByPrdId;
    }

    @PostMapping("/getSubProdImgWithDesc")
    public GenericResponse<List<Map<String, String>>> getSubProdImgWithDescByProdId(@RequestBody RequestDto requestDto,
                                                                                    @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
                                                                                    @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
                                                                                    @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
                                                                                    @RequestHeader(name = AppConstants.SCREENID) String screenId,
                                                                                    @RequestHeader(name = AppConstants.CHANNEL) String channel,
                                                                                    @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage
    ){
        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );

        GenericResponse<List<Map<String, String>>> subProdImgsWithDescByProdId = productTypeService.getSubProdImgsWithDescByProdId(requestDto.getRequestInfoDto().getId(),requestDto, headers);

        return subProdImgsWithDescByProdId;

    }


}