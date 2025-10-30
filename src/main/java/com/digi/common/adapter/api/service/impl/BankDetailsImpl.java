package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.BankDetailsService;
import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.digi.common.domain.model.dto.FollowUsItemDto;
import com.digi.common.domain.model.dto.SocialMedia;
import com.digi.common.domain.repository.BankDetailsRepository;
import com.digi.common.exception.ResourceNotFoundException;
import com.digi.common.infrastructure.persistance.BankDetailsEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "false")
public class BankDetailsImpl implements BankDetailsService {

    @Autowired
    private BankDetailsRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public BankDetailsResponseDto getBankDetails(String lang) {
        log.info("getBankDetails called with lang: {}", lang);

        log.debug("Attempting to fetch bank details entity for id=1");
        BankDetailsEntity entity = repository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("No bank details found"));

        BankDetailsResponseDto responseDto = new BankDetailsResponseDto();
        responseDto.setMail(entity.getMail());
        responseDto.setContact(entity.getContact());
        responseDto.setInternationalContact(entity.getInternationalContact());
        log.debug("Basic contact info set in response DTO");

        String followUsJson = entity.getFollowUsJson();

        if (followUsJson != null && !followUsJson.isBlank()) {
            log.debug("Parsing followUsJson for social media links");
            try {
                List<FollowUsItemDto> socialLinks = objectMapper.readValue(followUsJson, new TypeReference<>() { });

                List<SocialMedia> socialMedia = mapToSocialMediaList(entity, socialLinks, lang);
                responseDto.setFollowUs(socialMedia);

            } catch (JsonProcessingException e) {
                log.error("Failed to parse followUsJson: {}", followUsJson, e);
                throw new RuntimeException("Invalid followUsJson format", e);
            }
        } else {
            log.info("No social media followUsJson found in entity");
        }

        log.info("Returning bank details response DTO for lang: {}", lang);
        return responseDto;
    }

    public List<SocialMedia> mapToSocialMediaList(BankDetailsEntity entity, List<FollowUsItemDto> dtoList, String lang) {
        log.debug("Mapping {} FollowUsItemDto entries to SocialMedia objects for lang: {}", dtoList.size(), lang);
        List<SocialMedia> list = new ArrayList<>();

        boolean isEnglish = "en".equalsIgnoreCase(lang);

        for (int i = 0; i < dtoList.size(); i++) {
            FollowUsItemDto dto = dtoList.get(i);
            SocialMedia sm = new SocialMedia();

            sm.setName(isEnglish ? dto.getNameEn() : dto.getNameAr());
            sm.setDisplayImage(dto.getDisplayImage());
            sm.setDisplayOrder(dto.getDisplayOrder());

            switch (i) {
                case 0 -> sm.setUrl(isEnglish ? dto.getInstaUrlEN() : dto.getInstaUrlAR());
                case 1 -> sm.setUrl(isEnglish ? dto.getSnapUrlEN() : dto.getSnapUrlAR());
                case 2 -> sm.setUrl(isEnglish ? dto.getYoutubeUrlEN() : dto.getYoutubeUrlAR());
                case 3 -> sm.setUrl(isEnglish ? dto.getFacebookUrlEN() : dto.getFacebookUrlAR());
                case 4 -> sm.setUrl(isEnglish ? dto.getTwitterUrlEN() : dto.getTwitterUrlAR());
            }

            log.trace("Mapped social media: {}", sm);
            list.add(sm);
        }

        SocialMedia bankMedia = new SocialMedia();
        bankMedia.setName(isEnglish ? entity.getNameEn() : entity.getNameAr());
        bankMedia.setUrl(isEnglish ? entity.getUrlEn() : entity.getUrlAr());
        bankMedia.setDisplayImage(entity.getDisplayImage());
        bankMedia.setDisplayOrder(entity.getDisplayOrder());

        list.add(bankMedia);
        log.debug("Added bank's own social media: {}", bankMedia);
        list.sort(Comparator.comparingInt(SocialMedia::getDisplayOrder));
        log.debug("Sorted social media list by display order");

        return list;
    }

}
