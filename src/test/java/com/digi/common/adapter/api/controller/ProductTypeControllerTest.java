package com.digi.common.adapter.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.ProductTypeDto;
import com.digi.common.domain.model.dto.ProductImageDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ProductTypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductTypeService productTypeService;

    @InjectMocks
    private ProductTypeController controller;

    private ObjectMapper objectMapper;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        requestDto = new RequestDto();
        // populate requestDto with dummy data if needed
    }

    @Test
    void testGetActiveProducts_success() throws Exception {
        GenericResponse<List<ProductTypeDto>> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(new ResultUtilVO("000000","Success"));
        serviceResponse.setData(List.of(new ProductTypeDto()));

        when(productTypeService.getActiveProductTypes(
                org.mockito.Mockito.any(), org.mockito.Mockito.any(RequestDto.class)))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/products/retrieve-eligible-products")
                        .header(AppConstants.SERVICEID, "s")
                        .header(AppConstants.MODULE_ID, "m")
                        .header(AppConstants.SUB_MODULE_ID, "sm")
                        .header(AppConstants.SCREENID, "sc")
                        .header(AppConstants.CHANNEL, "ch")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetActiveProductImages_success() throws Exception {
        GenericResponse<List<ProductImageDto>> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(new ResultUtilVO("000000","Success"));
        serviceResponse.setData(List.of(new ProductImageDto()));

        when(productTypeService.getActiveProductImages(
                org.mockito.Mockito.any(), org.mockito.Mockito.any(RequestDto.class)))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/products/retrieve-all-images")
                        .header(AppConstants.SERVICEID, "s")
                        .header(AppConstants.MODULE_ID, "m")
                        .header(AppConstants.SUB_MODULE_ID, "sm")
                        .header(AppConstants.SCREENID, "sc")
                        .header(AppConstants.CHANNEL, "ch")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetProdOrSubProdImagesById_success() throws Exception {
        GenericResponse<Map<String,String>> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(new ResultUtilVO("000000","Success"));
        serviceResponse.setData(Map.of("image","img"));

        // Mock service
        when(productTypeService.getProdOrSubProdImgsById(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.any(RequestDto.class),
                org.mockito.Mockito.any()))
                .thenReturn(serviceResponse);

        // Ensure requestDto has RequestInfoDto with productType & id
        requestDto.setRequestInfoDto(new com.digi.common.domain.model.dto.RequestInfoDto());
        requestDto.getRequestInfoDto().setProductType("product");
        requestDto.getRequestInfoDto().setId(1L);

        mockMvc.perform(post("/products/retrive-image")
                        .header(AppConstants.SERVICEID, "s")
                        .header(AppConstants.MODULE_ID, "m")
                        .header(AppConstants.SUB_MODULE_ID, "sm")
                        .header(AppConstants.SCREENID, "sc")
                        .header(AppConstants.CHANNEL, "ch")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.data.image").value("img"));
    }

    @Test
    void testGetSubProdImgsByProdId_success() throws Exception {
        GenericResponse<List<Map<String,String>>> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(new ResultUtilVO("000000","Success"));
        serviceResponse.setData(List.of(Map.of("image","img")));

        requestDto.setRequestInfoDto(new com.digi.common.domain.model.dto.RequestInfoDto());
        requestDto.getRequestInfoDto().setId(1L);

        when(productTypeService.getSubProdImgsByPrdId(
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.any(RequestDto.class),
                org.mockito.Mockito.any()))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/products/getSubImgsByProdId")
                        .header(AppConstants.SERVICEID, "s")
                        .header(AppConstants.MODULE_ID, "m")
                        .header(AppConstants.SUB_MODULE_ID, "sm")
                        .header(AppConstants.SCREENID, "sc")
                        .header(AppConstants.CHANNEL, "ch")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.data[0].image").value("img"));
    }

    @Test
    void testGetSubProdImgWithDescByProdId_success() throws Exception {
        GenericResponse<List<Map<String,String>>> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(new ResultUtilVO("000000","Success"));
        serviceResponse.setData(List.of(Map.of("image","img","description","Desc")));

        requestDto.setRequestInfoDto(new com.digi.common.domain.model.dto.RequestInfoDto());
        requestDto.getRequestInfoDto().setId(1L);

        when(productTypeService.getSubProdImgsWithDescByProdId(
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.any(RequestDto.class),
                org.mockito.Mockito.any()))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/products/getSubProdImgWithDesc")
                        .header(AppConstants.SERVICEID, "s")
                        .header(AppConstants.MODULE_ID, "m")
                        .header(AppConstants.SUB_MODULE_ID, "sm")
                        .header(AppConstants.SCREENID, "sc")
                        .header(AppConstants.CHANNEL, "ch")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.data[0].image").value("img"))
                .andExpect(jsonPath("$.data[0].description").value("Desc"));
    }
}