package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.digi.common.domain.model.dto.FollowUsItemDto;
import com.digi.common.domain.repository.BankDetailsRepository;
import com.digi.common.exception.ResourceNotFoundException;
import com.digi.common.infrastructure.persistance.BankDetailsEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BankDetailsImplTest {

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BankDetailsImpl bankDetailsService;

    private BankDetailsEntity entity;

    private final String followUsJson = """
    [
      {
        "nameEn": "Instagram",
        "nameAr": "انستغرام",
        "displayImage": "insta.png",
        "displayOrder": 1,
        "instaUrlEn": "https://instagram.com/dukhanbank",
        "instaUrlAr": "https://instagram.com/dukhanbank_ar"
      }
    ]
    """;

    @BeforeEach
    void setUp() {

        // Arrange
        entity = new BankDetailsEntity();
        entity.setId(1L);
        entity.setNameEn("Dukhan Bank");
        entity.setNameAr("بنك دخان");
        entity.setMail("info@dukhanbank.com");
        entity.setContact(12345678L);
        entity.setInternationalContact("+9712345678");
        entity.setDisplayOrder(0);
        entity.setDisplayImage("bank.png");
        entity.setUrlEn("https://dukhanbank.com/en");
        entity.setUrlAr("https://dukhanbank.com/ar");
        entity.setFollowUsJson(followUsJson);

    }


    @Test
    void getBankDetails_WhenEntityFound_ReturnsResponseDto() throws Exception {

        FollowUsItemDto dto1 = FollowUsItemDto.builder()
                .nameEn("Instagram")
                .nameAr("انستغرام")
                .displayImage("insta.png")
                .displayOrder(1)
                .instaUrlEN("https://instagram.com/dukhanbank")
                .instaUrlAR("https://instagram.com/dukhanbank_ar")
                .build();

        FollowUsItemDto dto2 = FollowUsItemDto.builder()
                .nameAr("Snapchat")
                .nameEn("")
                .snapUrlAR("https://snapchat.com/dukhanbank")
                .snapUrlEN("https://snapchat.com/dukhanbank")
                .displayImage("snapchat.png")
                .displayOrder(2)
                .build();

        FollowUsItemDto dto3 = FollowUsItemDto.builder()
                .nameAr("Facebook")
                .nameEn("")
                .snapUrlAR("https://facebook.com/dukhanbank")
                .snapUrlEN("https://facebook.com/dukhanbank")
                .displayImage("facbook.png")
                .displayOrder(3)
                .build();

        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(any(String.class), any(TypeReference.class)))
                .thenReturn(List.of(dto1, dto2, dto3));
        BankDetailsResponseDto bankDetails = bankDetailsService.getBankDetails("en");
        assertNotNull(bankDetails);
        assertEquals("info@dukhanbank.com", bankDetails.getMail());
        assertEquals(12345678L, bankDetails.getContact());
        assertEquals("+9712345678", bankDetails.getInternationalContact());
        assertNotNull(bankDetails.getFollowUs());

        verify(bankDetailsRepository, times(1)).findById(1L);
        verify(objectMapper, times(1)).readValue(eq(followUsJson), any(TypeReference.class));
    }

    @Test
    void getBankDetails_ReturnException_WhenJsonFailedToParse() throws JsonProcessingException {

        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(any(String.class), any(TypeReference.class)))
                .thenThrow(new JsonProcessingException("Invalid followUsJson format") {});

        RuntimeException foundException = assertThrows(RuntimeException.class, () -> {
            bankDetailsService.getBankDetails("en");
        });

        assertNotNull(foundException);
        assertEquals("Invalid followUsJson format", foundException.getMessage());
        verify(bankDetailsRepository).findById(1L);
        verify(objectMapper).readValue(any(String.class), any(TypeReference.class));
    }

    @Test
    void getBankDetails_WhenNoEntityFound_ThrowsException() {

        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException foundException = assertThrows(ResourceNotFoundException.class, () -> {
            bankDetailsService.getBankDetails("en");
        });

        assertNotNull(foundException);
        assertEquals("No bank details found", foundException.getMessage());
        verify(bankDetailsRepository, times(1)).findById(1L);
    }
}