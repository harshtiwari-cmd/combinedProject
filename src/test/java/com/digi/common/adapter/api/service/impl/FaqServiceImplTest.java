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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    // ✅ 1. FAQs found for given language
    @Test
    void testGetFaqs_ReturnsFaqsForGivenLanguage() {
        DefaultHeadersDto headers = new DefaultHeadersDto();
        headers.setAcceptLanguage("en");
        RequestDto requestDto = new RequestDto();

        List<Faq> faqs = Arrays.asList(
                new Faq(1L, "Q1", "A1", "en"),
                new Faq(2L, "Q2", "A2", "en")
        );

        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(faqs);

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, requestDto);

        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getFaqList().size());
        assertEquals("Q1", response.getData().getFaqList().get(0).getQuestion());
        assertEquals("A1", response.getData().getFaqList().get(0).getAnswer());
        assertEquals("Q2", response.getData().getFaqList().get(1).getQuestion());
        assertEquals("A2", response.getData().getFaqList().get(1).getAnswer());

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
        verifyNoMoreInteractions(faqRepository);
    }

    // ✅ 2. No FAQs found → NOT_FOUND_CODE & NOT_FOUND_DESC
    @Test
    void testGetFaqs_NoFaqsFound() {
        DefaultHeadersDto headers = new DefaultHeadersDto();
        headers.setAcceptLanguage("en");
        RequestDto requestDto = new RequestDto();

        when(faqRepository.findByLangIgnoreCase("en")).thenReturn(Collections.emptyList());

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, requestDto);

        assertNotNull(response);
        assertEquals(AppConstant.NOT_FOUND_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.NOT_FOUND_DESC, response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertTrue(response.getData().getFaqList().isEmpty());

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
        verifyNoMoreInteractions(faqRepository);
    }

    // ✅ 3. Blank Accept-Language → uses DEFAULT_LANGUAGE
    @Test
    void testGetFaqs_BlankLanguage_UsesDefaultLang() {
        DefaultHeadersDto headers = new DefaultHeadersDto();
        headers.setAcceptLanguage(" "); // blank value
        RequestDto requestDto = new RequestDto();

        List<Faq> faqs = Collections.singletonList(
                new Faq(1L, "Q1", "A1", AppConstant.DEFAULT_LANGUAGE)
        );

        when(faqRepository.findByLangIgnoreCase(AppConstant.DEFAULT_LANGUAGE)).thenReturn(faqs);

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, requestDto);

        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getStatus().getDescription());
        assertEquals(1, response.getData().getFaqList().size());
        assertEquals("Q1", response.getData().getFaqList().get(0).getQuestion());
        assertEquals("A1", response.getData().getFaqList().get(0).getAnswer());

        verify(faqRepository, times(1)).findByLangIgnoreCase(AppConstant.DEFAULT_LANGUAGE);
        verifyNoMoreInteractions(faqRepository);
    }

    // ✅ 4. Null Accept-Language → uses DEFAULT_LANGUAGE
    @Test
    void testGetFaqs_NullLanguage_UsesDefaultLang() {
        DefaultHeadersDto headers = new DefaultHeadersDto();
        headers.setAcceptLanguage(null);
        RequestDto requestDto = new RequestDto();

        List<Faq> faqs = Collections.singletonList(
                new Faq(1L, "Q1", "A1", AppConstant.DEFAULT_LANGUAGE)
        );

        when(faqRepository.findByLangIgnoreCase(AppConstant.DEFAULT_LANGUAGE)).thenReturn(faqs);

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, requestDto);

        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getStatus().getDescription());
        assertEquals(1, response.getData().getFaqList().size());
        assertEquals("Q1", response.getData().getFaqList().get(0).getQuestion());

        verify(faqRepository, times(1)).findByLangIgnoreCase(AppConstant.DEFAULT_LANGUAGE);
        verifyNoMoreInteractions(faqRepository);
    }

    // ✅ 5. Exception handling
    @Test
    void testGetFaqs_ExceptionScenario() {
        DefaultHeadersDto headers = new DefaultHeadersDto();
        headers.setAcceptLanguage("en");
        RequestDto requestDto = new RequestDto();

        when(faqRepository.findByLangIgnoreCase("en")).thenThrow(new RuntimeException("DB error"));

        GenericResponse<FaqResponse> response = faqService.getFaqs(headers, requestDto);

        assertNotNull(response);
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());
        assertNull(response.getData());

        verify(faqRepository, times(1)).findByLangIgnoreCase("en");
        verifyNoMoreInteractions(faqRepository);
    }
}
