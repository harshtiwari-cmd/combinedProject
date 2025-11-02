package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.domain.model.ProductType;
import com.digi.common.domain.model.SubProduct;
import com.digi.common.domain.model.dto.*;
import com.digi.common.domain.repository.ProductTypeRepository;
import com.digi.common.domain.repository.SubProductRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    @Autowired
    private  ProductTypeRepository productTypeRepository;

    @Autowired
    private  SubProductRepository subProductRepo;


    @Override
    public GenericResponse<List<ProductTypeDto>> getActiveProductTypes(DefaultHeadersDto headers, RequestDto requestDto) {
        log.info("[START] getActiveProductTypes | Headers: {} | Request: {}", headers, requestDto);
        GenericResponse<List<ProductTypeDto>> response = new GenericResponse<>();
        try {
            String lang = headers.getAcceptLanguage() != null ? headers.getAcceptLanguage() : AppConstant.DEFAULT_LANGUAGE;
            log.debug("Using language: {}", lang);

            List<ProductType> productTypes = productTypeRepository.findAllActiveWithSubProducts();
            log.debug("Fetched {} active product types from repository", productTypes.size());

            List<ProductTypeDto> productDtos = productTypes.stream()
                    .map(pt -> mapProductTypeToDto(pt, lang))
                    .collect(Collectors.toList());

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(productDtos);

            log.info("[SUCCESS] getActiveProductTypes | Total DTOs: {}", productDtos.size());
        } catch (Exception e) {
            log.error("[ERROR] getActiveProductTypes failed", e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        log.info("[END] getActiveProductTypes");
        return response;
    }

    private ProductTypeDto mapProductTypeToDto(ProductType productType, String lang) {
        log.trace("Mapping ProductType to DTO | ID: {}", productType.getId());
        List<SubProductDto> subProducts = productType.getSubProducts().stream()
                .map(sp -> mapSubProductToDto(sp, lang))
                .collect(Collectors.toList());

        String base64Image = productType.getImage() != null
                ? Base64.getEncoder().encodeToString(productType.getImage())
                : null;

        String name = "ar".equalsIgnoreCase(lang) ? productType.getNameAr() : productType.getName();
        String category = "ar".equalsIgnoreCase(lang) ? productType.getCategoryAr() : productType.getProductCategory();

        log.trace("Mapped ProductType ID: {} -> DTO ready", productType.getId());
        return new ProductTypeDto(
                productType.getId(),
                name,
                category,
                base64Image,
                productType.isActive(),
                productType.getCreatedAt(),
                productType.getUpdatedAt(),
                subProducts
        );
    }

    private SubProductDto mapSubProductToDto(SubProduct subProduct, String lang) {
        log.trace("Mapping SubProduct to DTO | ID: {}", subProduct.getId());
        String name = "ar".equalsIgnoreCase(lang) ? subProduct.getNameAr() : subProduct.getName();
        String category = "ar".equalsIgnoreCase(lang) ? subProduct.getCategoryAr() : subProduct.getCategory();
        String description = "ar".equalsIgnoreCase(lang) ? subProduct.getDescriptionAr() : subProduct.getDescription();
        return new SubProductDto(
                subProduct.getId(),
                name,
                category,
                description,
                subProduct.isActive(),
                subProduct.getCreatedAt(),
                subProduct.getUpdatedAt()
        );
    }

    @Override
    public GenericResponse<List<ProductImageDto>> getActiveProductImages(DefaultHeadersDto headers, RequestDto requestDto) {
        log.info("[START] getActiveProductImages | Headers: {} | Request: {}", headers, requestDto);
        GenericResponse<List<ProductImageDto>> response = new GenericResponse<>();
        try {
            List<ProductType> productTypes = productTypeRepository.findAllActiveWithSubProducts();
            log.debug("Fetched {} active products for image mapping", productTypes.size());

            List<ProductImageDto> productImages = productTypes.stream()
                    .map(this::mapToProdImgDto)
                    .collect(Collectors.toList());

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(productImages);
            log.info("[SUCCESS] getActiveProductImages | Total Images: {}", productImages.size());
        } catch (Exception e) {
            log.error("[ERROR] getActiveProductImages failed", e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        log.info("[END] getActiveProductImages");
        return response;
    }

    private ProductImageDto mapToProdImgDto(ProductType productType) {
        log.trace("Mapping product image DTO | Product ID: {}", productType.getId());
        List<SubProductImageDto> subProds = productType.getSubProducts().stream()
                .filter(SubProduct::isActive)
                .map(this::mapToSubProdImgDto)
                .collect(Collectors.toList());
        String base64 = productType.getImage() != null
                ? Base64.getEncoder().encodeToString(productType.getImage()) : null;
        return new ProductImageDto(productType.getId(), base64, subProds);
    }

    private SubProductImageDto mapToSubProdImgDto(SubProduct subProduct) {
        log.trace("Mapping SubProduct image | ID: {}", subProduct.getId());
        String base64 = subProduct.getImage() != null
                ? Base64.getEncoder().encodeToString(subProduct.getImage()) : null;
        return new SubProductImageDto(subProduct.getId(), base64);
    }

    @Override
    public GenericResponse<Map<String, String>> getProdOrSubProdImgsById(String prodtType, Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        log.info("[START] getProdOrSubProdImgsById | Type: {} | ID: {}", prodtType, id);
        GenericResponse<Map<String, String>> response = new GenericResponse<>();
        try {
            if (prodtType == null || prodtType.isBlank() || isInvalidId(id)) {
                log.warn("Invalid input -> prodtType: {}, id: {}", prodtType, id);
                response.setStatus(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC));
                return response;
            }

            byte[] image = null;
            Map<String, String> map = new HashMap<>();
            switch (prodtType.toLowerCase()) {
                case "product":
                    log.debug("Fetching product image from repository | ID: {}", id);
                    image = productTypeRepository.findProductImageById(id);
                    break;
                case "subproduct":
                    log.debug("Fetching subproduct image from repository | ID: {}", id);
                    image = subProductRepo.findSubProductImageById(id);
                    break;
                default:
                    log.warn("Invalid product type received: {}", prodtType);
                    response.setStatus(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC + ": type must be product or subproduct"));
                    response.setData(null);
                    return response;
            }

            String base64 = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
            map.put("image", base64);
            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(map);
            log.info("[SUCCESS] getProdOrSubProdImgsById | ID: {}", id);
        } catch (Exception e) {
            log.error("[ERROR] getProdOrSubProdImgsById | Type: {} | ID: {}", prodtType, id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        log.info("[END] getProdOrSubProdImgsById");
        return response;
    }

    @Override
    public GenericResponse<List<Map<String, String>>> getSubProdImgsByPrdId(Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        log.info("[START] getSubProdImgsByPrdId | Product ID: {}", id);
        GenericResponse<List<Map<String, String>>> response = new GenericResponse<>();
        try {
            if (isInvalidId(id)) {
                log.warn("Invalid product ID: {}", id);
                response.setStatus(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC));
                return response;
            }

            List<byte[]> res = productTypeRepository.findSubProductImagesByProductId(id);
            log.debug("Fetched {} image records from DB", res.size());

            List<Map<String, String>> imageList = new ArrayList<>();
            for (byte[] imageBytes : res) {
                String base64Image = imageBytes != null ? Base64.getEncoder().encodeToString(imageBytes) : null;
                Map<String, String> imageMap = new HashMap<>();
                imageMap.put("image", base64Image);
                imageList.add(imageMap);
            }

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(imageList);
            log.info("[SUCCESS] getSubProdImgsByPrdId | Total: {}", imageList.size());
        } catch (Exception e) {
            log.error("[ERROR] getSubProdImgsByPrdId | Product ID: {}", id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        log.info("[END] getSubProdImgsByPrdId");
        return response;
    }

    @Override
    public GenericResponse<List<Map<String, String>>> getSubProdImgsWithDescByProdId(Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        log.info("[START] getSubProdImgsWithDescByProdId | Product ID: {}", id);
        GenericResponse<List<Map<String, String>>> response = new GenericResponse<>();
        try {
            if (isInvalidId(id)) {
                log.warn("Invalid product ID: {}", id);
                response.setStatus(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC));
                return response;
            }

            List<Object[]> results = productTypeRepository.findSubProductImageAndDescriptionsByProductId(id);
            log.debug("Fetched {} image+description pairs from DB", results.size());

            List<Map<String, String>> responseList = new ArrayList<>();
            for (Object[] obj : results) {
                byte[] imageBytes = (byte[]) obj[0];
                String descriptionEn = (String) obj[1];
                String descriptionAr = (String) obj[2];

                String base64Image = imageBytes != null ? Base64.getEncoder().encodeToString(imageBytes) : null;
                String description = "ar".equalsIgnoreCase(headers.getAcceptLanguage()) ? descriptionAr : descriptionEn;

                Map<String, String> subProdMap = new LinkedHashMap<>();
                subProdMap.put("image", base64Image);
                subProdMap.put("description", description);
                responseList.add(subProdMap);
            }

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(responseList);
            log.info("[SUCCESS] getSubProdImgsWithDescByProdId | Total: {}", responseList.size());
        } catch (Exception e) {
            log.error("[ERROR] getSubProdImgsWithDescByProdId | Product ID: {}", id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        log.info("[END] getSubProdImgsWithDescByProdId");
        return response;
    }

    private boolean isInvalidId(Long id) {
        log.trace("Validating ID: {}", id);
        return id == null;
    }
}
