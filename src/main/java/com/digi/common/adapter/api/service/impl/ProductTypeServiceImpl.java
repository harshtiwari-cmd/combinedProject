package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.ProductTypeService;
import com.digi.common.constants.AppConstants;
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
        GenericResponse<List<ProductTypeDto>> response = new GenericResponse<>();
        try {
            String lang = headers.getAcceptLanguage() != null ? headers.getAcceptLanguage() : AppConstant.DEFAULT_LANGUAGE;
            log.info("Fetching active product types | Language: {}", lang);

            List<ProductType> productTypes = productTypeRepository.findAllActiveWithSubProducts();
            log.debug("Fetched {} product types from DB", productTypes.size());

            List<ProductTypeDto> productDtos = productTypes.stream()
                    .map(pt -> mapProductTypeToDto(pt, lang))
                    .collect(Collectors.toList());

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(productDtos);

            log.info("Successfully fetched and mapped {} product types", productDtos.size());
        } catch (Exception e) {
            log.error("Error while fetching active product types", e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        return response;
    }

    private ProductTypeDto mapProductTypeToDto(ProductType productType, String lang) {
        List<SubProductDto> subProducts = productType.getSubProducts().stream()
                .map(sp -> mapSubProductToDto(sp, lang))
                .collect(Collectors.toList());

        String base64Image = productType.getImage() != null
                ? Base64.getEncoder().encodeToString(productType.getImage())
                : null;

        String name = "ar".equalsIgnoreCase(lang) ? productType.getNameAr() : productType.getName();
        String category = "ar".equalsIgnoreCase(lang) ? productType.getCategoryAr() : productType.getProductCategory();

        log.debug("Mapped ProductType ID: {} to DTO", productType.getId());

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
        String name = "ar".equalsIgnoreCase(lang) ? subProduct.getNameAr() : subProduct.getName();
        String category = "ar".equalsIgnoreCase(lang) ? subProduct.getCategoryAr() : subProduct.getCategory();
        String description = "ar".equalsIgnoreCase(lang) ? subProduct.getDescriptionAr() : subProduct.getDescription();

        log.debug("Mapped SubProduct ID: {} to DTO", subProduct.getId());

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
        GenericResponse<List<ProductImageDto>> response = new GenericResponse<>();
        try {
            log.info("Fetching active product images");
            List<ProductType> productTypes = productTypeRepository.findAllActiveWithSubProducts();
            log.debug("Fetched {} products for image mapping", productTypes.size());

            List<ProductImageDto> productImages = productTypes.stream()
                    .map(this::mapToProdImgDto)
                    .collect(Collectors.toList());

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(productImages);

            log.info("Successfully fetched {} product images", productImages.size());
        } catch (Exception e) {
            log.error("Error while fetching product images", e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        return response;
    }

    private ProductImageDto mapToProdImgDto(ProductType productType) {
        List<SubProductImageDto> subProds = productType.getSubProducts().stream()
                .filter(SubProduct::isActive)
                .map(this::mapToSubProdImgDto)
                .collect(Collectors.toList());

        String base64 = productType.getImage() != null
                ? Base64.getEncoder().encodeToString(productType.getImage()) : null;

        log.debug("Mapped Product ID: {} with {} active sub-products to image DTO", productType.getId(), subProds.size());
        return new ProductImageDto(productType.getId(), base64, subProds);
    }

    private SubProductImageDto mapToSubProdImgDto(SubProduct subProduct) {
        String base64 = subProduct.getImage() != null
                ? Base64.getEncoder().encodeToString(subProduct.getImage()) : null;
        log.debug("Mapped SubProduct ID: {} to image DTO", subProduct.getId());
        return new SubProductImageDto(subProduct.getId(), base64);
    }

    @Override
    public GenericResponse<Map<String, String>> getProdOrSubProdImgsById(String prodtType, Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        GenericResponse<Map<String, String>> response = new GenericResponse<>();
        try {
            log.info("Fetching image for {} with ID: {}", prodtType, id);
            byte[] image = null;
            Map<String, String> map = new HashMap<>();

            switch (prodtType.toLowerCase()) {
                case "product":
                    image = productTypeRepository.findProductImageById(id);
                    break;
                case "subproduct":
                    image = subProductRepo.findSubProductImageById(id);
                    break;
                default:
                    log.warn("Invalid product type: {}", prodtType);
                    response.setStatus(new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC + ": type must be product or subproduct"));
                    response.setData(null);
                    return response;
            }

            String base64 = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
            map.put("image", base64);
            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(map);

            log.info("Successfully fetched image for {} ID: {}", prodtType, id);
        } catch (Exception e) {
            log.error("Error while fetching image for {} ID: {}", prodtType, id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        return response;
    }

    @Override
    public GenericResponse<List<Map<String, String>>> getSubProdImgsByPrdId(Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        GenericResponse<List<Map<String, String>>> response = new GenericResponse<>();
        try {
            log.info("Fetching subproduct images for Product ID: {}", id);
            List<byte[]> res = productTypeRepository.findSubProductImagesByProductId(id);

            List<Map<String, String>> imageList = new ArrayList<>();
            for (byte[] imageBytes : res) {
                String base64Image = imageBytes != null
                        ? Base64.getEncoder().encodeToString(imageBytes)
                        : null;

                Map<String, String> imageMap = new HashMap<>();
                imageMap.put("image", base64Image);
                imageList.add(imageMap);
            }

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(imageList);

            log.info("Fetched {} subproduct images for Product ID: {}", imageList.size(), id);
        } catch (Exception e) {
            log.error("Error while fetching subproduct images for Product ID: {}", id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        return response;
    }

    @Override
    public GenericResponse<List<Map<String, String>>> getSubProdImgsWithDescByProdId(Long id, RequestDto requestDto, DefaultHeadersDto headers) {
        GenericResponse<List<Map<String, String>>> response = new GenericResponse<>();
        try {
            log.info("Fetching subproduct images with descriptions for Product ID: {}", id);
            List<Object[]> results = productTypeRepository.findSubProductImageAndDescriptionsByProductId(id);

            List<Map<String, String>> responseList = new ArrayList<>();

            for (Object[] obj : results) {
                byte[] imageBytes = (byte[]) obj[0];
                String descriptionEn = (String) obj[1];
                String descriptionAr = (String) obj[2];

                String base64Image = imageBytes != null
                        ? Base64.getEncoder().encodeToString(imageBytes)
                        : null;

                String description = "ar".equalsIgnoreCase(headers.getAcceptLanguage())
                        ? descriptionAr
                        : descriptionEn;

                Map<String, String> subProdMap = new LinkedHashMap<>();
                subProdMap.put("image", base64Image);
                subProdMap.put("description", description);

                responseList.add(subProdMap);
            }

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(responseList);

            log.info("Fetched {} subproduct image+description records for Product ID: {}", responseList.size(), id);
        } catch (Exception e) {
            log.error("Error while fetching subproduct images with description for Product ID: {}", id, e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }
        return response;
    }
}
