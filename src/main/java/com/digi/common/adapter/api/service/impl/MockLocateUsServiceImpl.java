package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.persistance.LocateUsImages;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "true")
public class MockLocateUsServiceImpl implements LocateUsService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CompletableFuture<Map<String, List<LocateUsDTO>>> fetchAllTypesAsync(String lang) {
        log.info("Mock fetchAllTypesAsync called for language: {}", lang);

        List<LocateUsDTO> branches = fetchByType(AppConstant.BRANCH, lang);
        List<LocateUsDTO> atms = fetchByType(AppConstant.ATM, lang);
        List<LocateUsDTO> kiosks = fetchByType(AppConstant.KIOSK, lang);

        Map<String, List<LocateUsDTO>> result = new HashMap<>();
        result.put(AppConstant.BRANCHES, branches);
        result.put(AppConstant.ATMS, atms);
        result.put(AppConstant.KIOSKS, kiosks);

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public String getImageForType(String locatorType) {
        if (locatorType == null || locatorType.isEmpty()) {
            throw new IllegalArgumentException("Locator type must not be null or empty");
        }

        try {
            ClassPathResource resource = new ClassPathResource("JSON/locate-us-images.json");

            List<LocateUsImages> usImages = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<LocateUsImages>>() {}
            );

            for (LocateUsImages img : usImages) {
                if (locatorType.equalsIgnoreCase(img.getLocatorType())) {
                    return img.getImage();
                }
            }
        } catch (IOException e) {
            log.error("Mock fetchAllTypesAsync called for language: {}", e.getMessage());
        }

        return "";
    }

    public List<LocateUsDTO> fetchByType(String locatorType, String lang) {
        String type = locatorType != null ? locatorType.toUpperCase() : "";
        String language = AppConstant.LANGUAGE_IN_AR.equalsIgnoreCase(lang) ? "AR" : "EN";

        String fileName = switch (type) {
            case AppConstant.BRANCH -> "mock-%s-branch.json".formatted(language);
            case AppConstant.ATM -> "mock-%s-atm.json".formatted(language);
            case AppConstant.KIOSK -> "mock-%s-kiosk.json".formatted(language);
            default -> null;
        };

        if (fileName == null) {
            log.warn("Unsupported locator type: '{}'. Returning empty list.", locatorType);
            return List.of();
        }

        log.info("Loading mock data for type: '{}' and language: '{}'", type, language);

        try {
            ClassPathResource resource = new ClassPathResource("JSON/" + fileName);
            List<LocateUsDTO> data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
            log.debug("Successfully loaded {} items from {}", data.size(), fileName);
            return data;
        } catch (IOException e) {
            log.error("Failed to load mock data from file: {}", fileName, e);
            return List.of();
        }
    }

}