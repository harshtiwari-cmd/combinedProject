package com.digi.common.adapter.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.digi.common.domain.model.ProductType;
import com.digi.common.domain.model.SubProduct;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.ProductImageDto;
import com.digi.common.domain.model.dto.ProductTypeDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.repository.ProductTypeRepository;
import com.digi.common.domain.repository.SubProductRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;

@ExtendWith(MockitoExtension.class)
class ProductTypeServiceImplTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private SubProductRepository subProductRepo;

    @InjectMocks
    private ProductTypeServiceImpl service;

    private DefaultHeadersDto headersEn;
    private DefaultHeadersDto headersAr;
    private RequestDto request;

    private ProductType product;
    private SubProduct subProduct;

    @BeforeEach
    void setup() {
        headersEn = new DefaultHeadersDto("s", "m", "sm", "sc", "ch", "en");
        headersAr = new DefaultHeadersDto("s", "m", "sm", "sc", "ch", "ar");
        request = new RequestDto();

        subProduct = new SubProduct();
        subProduct.setId(1L);
        subProduct.setName("Debit");
        subProduct.setCategory("Bank");
        subProduct.setNameAr("بطاقة");
        subProduct.setCategoryAr("بنك");
        subProduct.setImage("img".getBytes());
        subProduct.setDescription("English description");
        subProduct.setDescriptionAr("وصف عربي");

        product = new ProductType();
        product.setId(1L);
        product.setName("Cards");
        product.setProductCategory("Finance");
        product.setNameAr("بطاقات");
        product.setCategoryAr("مالية");
        product.setImage("img".getBytes());
        product.setSubProducts(List.of(subProduct));
    }

    // ---------------- getActiveProductTypes ----------------
    @Test
    void testGetActiveProductTypes_successEnglish() {
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));
        GenericResponse<List<ProductTypeDto>> res = service.getActiveProductTypes(headersEn, request);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals(1, res.getData().size());
        assertEquals("Cards", res.getData().get(0).getProductName());
    }

    @Test
    void testGetActiveProductTypes_successArabic() {
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));
        GenericResponse<List<ProductTypeDto>> res = service.getActiveProductTypes(headersAr, request);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals("بطاقات", res.getData().get(0).getProductName());
    }

    @Test
    void testGetActiveProductTypes_noImage() {
        product.setImage(null);
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));

        GenericResponse<List<ProductTypeDto>> res = service.getActiveProductTypes(headersEn, request);
        assertNull(res.getData().get(0).getProductImage());
    }

    @Test
    void testGetActiveProductTypes_noSubProducts() {
        product.setSubProducts(List.of());
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));

        GenericResponse<List<ProductTypeDto>> res = service.getActiveProductTypes(headersEn, request);
        assertTrue(res.getData().get(0).getSubProducts().isEmpty());
    }

    @Test
    void testGetActiveProductTypes_exception() {
        when(productTypeRepository.findAllActiveWithSubProducts()).thenThrow(new RuntimeException());
        GenericResponse<List<ProductTypeDto>> res = service.getActiveProductTypes(headersEn, request);

        assertEquals(AppConstant.GEN_ERROR_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }

    // ---------------- getActiveProductImages ----------------
    @Test
    void testGetActiveProductImages_success() {
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));
        GenericResponse<List<ProductImageDto>> res = service.getActiveProductImages(headersEn, request);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals(1, res.getData().size());
    }

    @Test
    void testGetActiveProductImages_nullImage() {
        product.setImage(null);
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));

        GenericResponse<List<ProductImageDto>> res = service.getActiveProductImages(headersEn, request);
        assertNull(res.getData().get(0).getProductImage());
    }

    @Test
    void testGetActiveProductImages_noSubProducts() {
        product.setSubProducts(List.of());
        when(productTypeRepository.findAllActiveWithSubProducts()).thenReturn(List.of(product));

        GenericResponse<List<ProductImageDto>> res = service.getActiveProductImages(headersEn, request);
        assertTrue(res.getData().get(0).getSubProductImages().isEmpty());
    }

    @Test
    void testGetActiveProductImages_exception() {
        when(productTypeRepository.findAllActiveWithSubProducts()).thenThrow(new RuntimeException());
        GenericResponse<List<ProductImageDto>> res = service.getActiveProductImages(headersEn, request);

        assertEquals(AppConstant.GEN_ERROR_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }

    // ---------------- getProdOrSubProdImgsById ----------------
    @Test
    void testGetProdOrSubProdImgsById_product() {
        when(productTypeRepository.findProductImageById(1L)).thenReturn("img".getBytes());

        GenericResponse<Map<String, String>> res = service.getProdOrSubProdImgsById("product", 1L, request, headersEn);
        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertNotNull(res.getData().get("image"));
    }

    @Test
    void testGetProdOrSubProdImgsById_subProduct() {
        when(subProductRepo.findSubProductImageById(1L)).thenReturn("img".getBytes());

        GenericResponse<Map<String, String>> res = service.getProdOrSubProdImgsById("subproduct", 1L, request, headersEn);
        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertNotNull(res.getData().get("image"));
    }

    @Test
    void testGetProdOrSubProdImgsById_invalidType() {
        GenericResponse<Map<String, String>> res = service.getProdOrSubProdImgsById("unknown", 1L, request, headersEn);
        assertEquals(AppConstant.BAD_REQUEST_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }

    @Test
    void testGetProdOrSubProdImgsById_nullImage() {
        when(productTypeRepository.findProductImageById(1L)).thenReturn(null);
        GenericResponse<Map<String, String>> res = service.getProdOrSubProdImgsById("product", 1L, request, headersEn);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertNull(res.getData().get("image"));
    }

    @Test
    void testGetProdOrSubProdImgsById_exception() {
        when(productTypeRepository.findProductImageById(1L)).thenThrow(new RuntimeException());
        GenericResponse<Map<String, String>> res = service.getProdOrSubProdImgsById("product", 1L, request, headersEn);

        assertEquals(AppConstant.GEN_ERROR_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }

    // ---------------- getSubProdImgsByPrdId ----------------
    @Test
    void testGetSubProdImgsByPrdId_success() {
        when(productTypeRepository.findSubProductImagesByProductId(1L))
                .thenReturn(List.of("img".getBytes(), "img2".getBytes()));

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsByPrdId(1L, request, headersEn);
        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals(2, res.getData().size());
    }

    @Test
    void testGetSubProdImgsByPrdId_empty() {
        when(productTypeRepository.findSubProductImagesByProductId(1L)).thenReturn(List.of());

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsByPrdId(1L, request, headersEn);
        assertTrue(res.getData().isEmpty());
    }

    @Test
    void testGetSubProdImgsByPrdId_exception() {
        when(productTypeRepository.findSubProductImagesByProductId(1L)).thenThrow(new RuntimeException());

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsByPrdId(1L, request, headersEn);
        assertEquals(AppConstant.GEN_ERROR_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }

    // ---------------- getSubProdImgsWithDescByProdId ----------------
    @Test
    void testGetSubProdImgsWithDescByProdId_english() {
        Object[] obj1 = {"img".getBytes(), "DescEn", "DescAr"};
        Object[] obj2 = {"img1".getBytes(), "DescEn2", "DescAr2"};
        when(productTypeRepository.findSubProductImageAndDescriptionsByProductId(1L))
                .thenReturn(List.of(obj1, obj2));

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsWithDescByProdId(1L, request, headersEn);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals("DescEn", res.getData().get(0).get("description"));
    }

    @Test
    void testGetSubProdImgsWithDescByProdId_arabic() {
        Object[] obj1 = {"img".getBytes(), "DescEn", "DescAr"};
        List<Object[]>al=   new ArrayList<>();
        al.add(obj1);

        when(productTypeRepository.findSubProductImageAndDescriptionsByProductId(1L))
                .thenReturn(al);

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsWithDescByProdId(1L, request, headersAr);

        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertEquals("DescAr", res.getData().get(0).get("description"));
    }

    @Test
    void testGetSubProdImgsWithDescByProdId_nullImage() {
        Object[] obj = {null, "DescEn", "DescAr"};
        List<Object[]>al=   new ArrayList<>();
        al.add(obj);
        when(productTypeRepository.findSubProductImageAndDescriptionsByProductId(1L))
                .thenReturn(al);

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsWithDescByProdId(1L, request, headersEn);
        assertEquals(AppConstant.RESULT_CODE, res.getStatus().getCode());
        assertNull(res.getData().get(0).get("image"));
    }

    @Test
    void testGetSubProdImgsWithDescByProdId_empty() {
        when(productTypeRepository.findSubProductImageAndDescriptionsByProductId(1L))
                .thenReturn(List.of());

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsWithDescByProdId(1L, request, headersEn);
        assertTrue(res.getData().isEmpty());
    }

    @Test
    void testGetSubProdImgsWithDescByProdId_exception() {
        when(productTypeRepository.findSubProductImageAndDescriptionsByProductId(1L))
                .thenThrow(new RuntimeException());

        GenericResponse<List<Map<String, String>>> res = service.getSubProdImgsWithDescByProdId(1L, request, headersEn);
        assertEquals(AppConstant.GEN_ERROR_CODE, res.getStatus().getCode());
        assertNull(res.getData());
    }
}
