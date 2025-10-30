package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.ProductImageDto;
import com.digi.common.domain.model.dto.ProductTypeDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;

import java.util.List;
import java.util.Map;


public interface ProductTypeService {
    GenericResponse<List<ProductTypeDto>> getActiveProductTypes(DefaultHeadersDto headers, RequestDto requestDto);

    GenericResponse<List<ProductImageDto>> getActiveProductImages(DefaultHeadersDto headers, RequestDto requestDto);

    GenericResponse<Map<String,String>>  getProdOrSubProdImgsById(String prodtType,Long id,RequestDto requestDto,DefaultHeadersDto headers);

    GenericResponse<List<Map<String, String>>> getSubProdImgsByPrdId(Long id, RequestDto requestDto,DefaultHeadersDto headers);

    GenericResponse<List<Map<String, String>>> getSubProdImgsWithDescByProdId(Long id, RequestDto requestDto, DefaultHeadersDto headers);


}