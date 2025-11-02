package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.domain.model.dto.*;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.exception.GlobalExceptionHandler;
import com.digi.common.infrastructure.common.AppConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@Disabled
class ProductTypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductTypeService productTypeService;

    @InjectMocks
    private ProductTypeController productTypeController;

    private ObjectMapper objectMapper;
    private RequestDto validRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productTypeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        DeviceInfoDto device = new DeviceInfoDto();
        device.setDeviceId("D123");
        device.setIpAddress("1.1.1.1");
        device.setOsType("Android");
        device.setOsVersion("14");
        device.setAppVersion("1.0");
        device.setVendorId("Vendor");
        device.setEndToEndId("E2E-001");

        RequestInfoDto reqInfo = new RequestInfoDto();
        reqInfo.setId(100L);
        reqInfo.setProductType("Loan");

        validRequest = new RequestDto();
        validRequest.setDeviceInfo(device);
        validRequest.setRequestInfoDto(reqInfo);
    }

    // Utility: common headers
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withValidHeaders(String url) {
        return post(url)
                .header("serviceId", "SRV-001")
                .header("moduleId", "MOD-101")
                .header("subModuleId", "SUB-202")
                .header("screenId", "SCR-303")
                .header("channel", "WEB")
                .header("accept-language", "en")
                .contentType(MediaType.APPLICATION_JSON);
    }

    // ----------------------------------------------------------------------
    // 1️ /retrieve-eligible-products
    // ----------------------------------------------------------------------
    @Test
    void testRetrieveEligibleProducts_success() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(new ProductTypeDto()));
        when(productTypeService.getActiveProductTypes(any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testRetrieveEligibleProducts_missingHeaders() throws Exception {
        mockMvc.perform(post("/products/retrieve-eligible-products")
                        .header("serviceId", "")
                        .header("moduleId", "")
                        .header("subModuleId", "")
                        .header("screenId", "")
                        .header("channel", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testRetrieveEligibleProducts_missingDeviceInfo() throws Exception {
        validRequest.setDeviceInfo(null);
        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstant.DEVICE_INFO_DESC));
    }

    @Test
    void testRetrieveEligibleProducts_serviceThrowsException() throws Exception {
        when(productTypeService.getActiveProductTypes(any(), any())).thenThrow(new RuntimeException("Simulated"));
        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError());
    }


    // ----------------------------------------------------------------------
    // 2️ /retrieve-all-images
    // ----------------------------------------------------------------------
    @Test
    void testRetrieveAllImages_success() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(new ProductImageDto()));
        when(productTypeService.getActiveProductImages(any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrieve-all-images")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testRetrieveAllImages_missingHeaders() throws Exception {
        mockMvc.perform(post("/products/retrieve-all-images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testRetrieveAllImages_missingDeviceInfo() throws Exception {
        validRequest.setDeviceInfo(null);
        mockMvc.perform(withValidHeaders("/products/retrieve-all-images")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    // ----------------------------------------------------------------------
    // 3️  /retrive-image
    // ----------------------------------------------------------------------
    @Test
    void testRetrieveImage_success() throws Exception {
        GenericResponse<Map<String, String>> response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                Map.of("img","img1"));
        when(productTypeService.getProdOrSubProdImgsById(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrive-image")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testRetrieveImage_missingHeaders() throws Exception {
        mockMvc.perform(post("/products/retrive-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testRetrieveImage_missingDeviceInfo() throws Exception {
        validRequest.setDeviceInfo(null);
        mockMvc.perform(withValidHeaders("/products/retrive-image")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testRetrieveImage_missingRequestInfo() throws Exception {
        validRequest.setRequestInfoDto(null);
        mockMvc.perform(withValidHeaders("/products/retrive-image")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testRetrieveImage_serviceThrows() throws Exception {
        when(productTypeService.getProdOrSubProdImgsById(any(),any(), any(),any()))
                .thenThrow(new RuntimeException("boom"));
        mockMvc.perform(withValidHeaders("/products/retrive-image")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.GEN_ERROR_CODE));
    }

    // ----------------------------------------------------------------------
    // 4️ /getSubImgsByProdId
    // ----------------------------------------------------------------------
    @Test
    void testGetSubImgsByProdId_success() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(Map.of("image", "img1")));
        when(productTypeService.getSubProdImgsByPrdId(any(), any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/getSubImgsByProdId")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testGetSubImgsByProdId_missingHeaders() throws Exception {
        mockMvc.perform(post("/products/getSubImgsByProdId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testGetSubImgsByProdId_missingDeviceInfo() throws Exception {
        validRequest.setDeviceInfo(null);
        mockMvc.perform(withValidHeaders("/products/getSubImgsByProdId")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testGetSubImgsByProdId_missingRequestInfo() throws Exception {
        validRequest.setRequestInfoDto(null);
        mockMvc.perform(withValidHeaders("/products/getSubImgsByProdId")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    // ----------------------------------------------------------------------
    // 5️ /getSubProdImgWithDesc
    // ----------------------------------------------------------------------
    @Test
    void testGetSubProdImgWithDesc_success() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, "Success"),
                List.of(Map.of("image", "img", "description", "desc")));
        when(productTypeService.getSubProdImgsWithDescByProdId(any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/getSubProdImgWithDesc")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE))
                .andExpect(jsonPath("$.data[0].description").value("desc"));
    }

    @Test
    void testGetSubProdImgWithDesc_missingHeaders() throws Exception {
        mockMvc.perform(post("/products/getSubProdImgWithDesc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testGetSubProdImgWithDesc_missingDeviceInfo() throws Exception {
        validRequest.setDeviceInfo(null);
        mockMvc.perform(withValidHeaders("/products/getSubProdImgWithDesc")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testGetSubProdImgWithDesc_missingRequestInfo() throws Exception {
        validRequest.setRequestInfoDto(null);
        mockMvc.perform(withValidHeaders("/products/getSubProdImgWithDesc")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE));
    }

    @Test
    void testGetSubProdImgWithDesc_serviceThrows() throws Exception {
        when(productTypeService.getSubProdImgsWithDescByProdId(any(), any(), any()))
                .thenThrow(new RuntimeException("Fail"));
        mockMvc.perform(withValidHeaders("/products/getSubProdImgWithDesc")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.GEN_ERROR_CODE));
    }




    // ----------------------------------------------------------------------
    // Language mapping tests
    // ----------------------------------------------------------------------

    @Test
    void testRetrieveEligibleProducts_languageMapping_en() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(new ProductTypeDto()));
        when(productTypeService.getActiveProductTypes(any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .header("accept-language", "english")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testRetrieveEligibleProducts_languageMapping_ar() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(new ProductTypeDto()));
        when(productTypeService.getActiveProductTypes(any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .header("accept-language", "arabic")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }

    @Test
    void testRetrieveEligibleProducts_languageMapping_default() throws Exception {
        var response = new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                List.of(new ProductTypeDto()));
        when(productTypeService.getActiveProductTypes(any(), any())).thenReturn(response);

        mockMvc.perform(withValidHeaders("/products/retrieve-eligible-products")
                        .header("accept-language", "unknownLang")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstant.RESULT_CODE));
    }


}