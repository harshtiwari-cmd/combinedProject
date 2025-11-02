package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.Faq;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.FaqResponse;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.repository.FaqRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FaqServiceImplTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqServiceImpl faqService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test 1: Successful fetch with supported language (en)
    @Test
    void testGetFaqs_SuccessfulFetch() {
        DefaultHeadersDto headers = new DefaultHeadersDto("1", "1", "1", "1", "WEB", "en");
        RequestDto request = new RequestDto();

        List<Faq> mockFaqs = List.of(
                new Faq(1L, "What is OTP?", "OTP means One Time Password", "en"),
                new Faq(2L, "How to register?", "Click Register on home screen", "en")
        );

        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(mockFaqs);

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, request);

        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getFaqList()).hasSize(2);
        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.RESULT_CODE);
        assertThat(response.getStatus().getDescription()).isEqualTo(AppConstant.RESULT_DESC);

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
    }

    // ✅ Test 2: Empty list returns NOT FOUND
    @Test
    void testGetFaqs_NoDataFound() {
        DefaultHeadersDto headers = new DefaultHeadersDto("1", "1", "1", "1", "WEB", "en");
        RequestDto request = new RequestDto();

        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(List.of());

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, request);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.NOT_FOUND_CODE);
        assertThat(response.getStatus().getDescription()).isEqualTo(AppConstant.NOT_FOUND_DESC);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getFaqList()).isEmpty();

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
    }

    // ✅ Test 3: Exception handling
    @Test
    void testGetFaqs_ExceptionThrown() {
        DefaultHeadersDto headers = new DefaultHeadersDto("1", "1", "1", "1", "WEB", "en");
        RequestDto request = new RequestDto();

        when(faqRepository.findByLangIgnoreCase("en")).thenThrow(new RuntimeException("DB connection failed"));

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, request);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.GEN_ERROR_CODE);
        assertThat(response.getStatus().getDescription()).isEqualTo(AppConstant.GEN_ERROR_DESC);
        assertThat(response.getData()).isNull();

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
    }

    // ✅ Test 4: Unsupported language defaults to English
    @Test
    void testGetFaqs_UnsupportedLanguageDefaultsToEnglish() {
        DefaultHeadersDto headers = new DefaultHeadersDto("1", "1", "1", "1", "WEB", "fr"); // unsupported
        RequestDto request = new RequestDto();

        List<Faq> mockFaqs = List.of(
                new Faq(1L, "What is OTP?", "OTP means One Time Password", "en")
        );

        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(mockFaqs);

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, request);

        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getFaqList()).hasSize(1);
        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.RESULT_CODE);
        assertThat(response.getStatus().getDescription()).isEqualTo(AppConstant.RESULT_DESC);

        // Verify that English repository call was made
        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
    }

    // ✅ Test 5: Null or blank language defaults to English
    @Test
    void testGetFaqs_NullOrBlankLanguageDefaultsToEnglish() {
        RequestDto request = new RequestDto();

        // Case 1: Null language
        DefaultHeadersDto headersNull = new DefaultHeadersDto("1", "1", "1", "1", "WEB", null);
        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(List.of());
        faqService.getFaqs(headersNull, request);
        verify(faqRepository, times(1)).findByLangIgnoreCase("en");

        // Case 2: Blank language
        DefaultHeadersDto headersBlank = new DefaultHeadersDto("1", "1", "1", "1", "WEB", "   ");
        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(List.of());
        faqService.getFaqs(headersBlank, request);
        verify(faqRepository, times(2)).findByLangIgnoreCase("en"); // second call
    }
}
