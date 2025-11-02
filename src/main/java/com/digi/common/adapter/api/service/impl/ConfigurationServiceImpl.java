//package com.digi.common.adapter.api.service.impl;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import com.digi.common.adapter.api.service.ConfigurationService;
//import com.digi.common.dto.ApiResponse;
//import com.digi.common.dto.ConfigurationDto;
//import com.digi.common.entity.Configuration;
//import com.digi.common.entity.ConfigurationFieldOptions;
//import com.digi.common.entity.ScreenDetails;
//import com.digi.common.exception.BusinessException;
//import com.digi.common.repository.ConfigurationFieldOptionsRepository;
//import com.digi.common.repository.ConfigurationRepository;
//import com.digi.common.repository.ScreenDetailsRepository;
//
//@Service
//public class ConfigurationServiceImpl implements ConfigurationService {
//
//    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
//
//    @Autowired
//    private ConfigurationRepository configurationRepository;
//
//    @Autowired
//    private ConfigurationFieldOptionsRepository configurationFieldOptionsRepository;
//
//    @Autowired
//    private ScreenDetailsRepository screenDetailsRepository;
//
//    @Override
//    public ApiResponse<ConfigurationDto> getRequestCallbackFields(String screenName, String lang) {
//        logger.info("Retrieving callback fields for screen: {} and language: {}", screenName, lang);
//
//        // Validate input parameters
//        if (screenName == null || screenName.trim().isEmpty()) {
//            throw new BusinessException("Screen name is required", "INVALID_SCREEN_NAME");
//        }
//
//        if (lang == null || lang.trim().isEmpty()) {
//            throw new BusinessException("Language is required", "INVALID_LANGUAGE");
//        }
//
//        // Normalize language code
//        String normalizedLang = lang.trim().toUpperCase();
//        if (!"EN".equals(normalizedLang) && !"AR".equals(normalizedLang)) {
//            throw new BusinessException("Invalid language code. Supported values: EN, AR", "INVALID_LANGUAGE");
//        }
//
//        try {
//            // Step 1: Get actual screenId from screen_details table using screenName (case-insensitive)
//            Optional<ScreenDetails> screenDetails = screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName.trim(), true);
//
//            if (screenDetails.isEmpty()) {
//                throw new BusinessException("Screen not found: " + screenName, "SCREEN_NOT_FOUND");
//            }
//
//            Long screenId = screenDetails.get().getScreenId();
//
//            // Step 2: Get configurations for the screenId
//            List<Configuration> configurations = configurationRepository.findByScreenIdAndIsActiveOrderBySequence(screenId, true);
//
//            if (CollectionUtils.isEmpty(configurations)) {
//                return ApiResponse.noDataFound();
//            }
//
//            List<ConfigurationDto> fields = configurations.stream()
//                    .map(config -> mapToDto(config, normalizedLang))
//                    .collect(Collectors.toList());
//
//            logger.info("Successfully retrieved {} callback fields for screen: {}", fields.size(), screenName);
//            return ApiResponse.success(fields);
//
//        } catch (BusinessException e) {
//            logger.error("Business error retrieving callback fields for screen: {} - {}", screenName, e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            logger.error("System error retrieving callback fields for screen: {}", screenName, e);
//            throw new BusinessException("Failed to retrieve callback fields", "SYSTEM_ERROR", e);
//        }
//    }
//
//    private ConfigurationDto mapToDto(Configuration config, String lang) {
//        try {
//            ConfigurationDto dto = new ConfigurationDto();
//
//            // Set fieldKey
//            dto.setFieldKey(config.getFieldKey());
//
//            // Set fieldName based on language
//            if ("AR".equals(lang) && config.getArabicFieldName() != null) {
//                dto.setFieldName(config.getArabicFieldName());
//            } else {
//                dto.setFieldName(config.getEnglishFieldName());
//            }
//
//            // Set fieldOption, fieldLength, fieldValidations, fieldType, sequence
//            dto.setFieldOption(config.getFieldOption());
//            dto.setFieldLength(config.getFieldLength());
//            dto.setFieldValidations(config.getFieldValidations());
//            dto.setFieldType(config.getFieldType());
//            dto.setSequence(config.getSequence());
//            dto.setErrMessage(config.getErrMessage());
//            dto.setRequiresProfanityCheck(config.getRequiresProfanityCheck());
//
//            // Set fieldOptions based on language and configurationId
//            if (config.getFieldType() != null) {
//                String type = config.getFieldType().trim();
//                if ("combo".equalsIgnoreCase(type) || "dropdown".equalsIgnoreCase(type)) {
//                    List<ConfigurationFieldOptions> fieldOptions = configurationFieldOptionsRepository
//                            .findByConfigurationIdAndIsActive(config.getId(), true);
//
//                    if (!CollectionUtils.isEmpty(fieldOptions)) {
//                        List<String> options = fieldOptions.stream()
//                                .map(option -> "AR".equals(lang) ?
//                                        option.getArabicOptionValue() : option.getEnglishOptionValue())
//                                .collect(Collectors.toList());
//                        dto.setFieldList(options);
//                    }
//                }
//            }
//
//            return dto;
//        } catch (Exception e) {
//            logger.error("Error mapping configuration to DTO for config ID: {} - {}", config.getId(), e.getMessage());
//            throw new BusinessException("Error processing configuration data", "MAPPING_ERROR", e);
//        }
//    }
//}
