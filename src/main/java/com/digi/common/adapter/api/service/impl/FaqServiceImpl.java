package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.FaqService;
import com.digi.common.domain.model.Faq;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.FaqDTO;
import com.digi.common.domain.model.dto.FaqResponse;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.repository.FaqRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class FaqServiceImpl implements FaqService {

    @Autowired
    private FaqRepository faqRepository;


    @Override
    public GenericResponse<FaqResponse> getFaqs(DefaultHeadersDto headers, RequestDto requestDto) {
        GenericResponse<FaqResponse> response = new GenericResponse<>();

        try {
            // Normalize language
            String lang = (headers.getAcceptLanguage() == null || headers.getAcceptLanguage().isBlank())
                    ? AppConstant.DEFAULT_LANGUAGE
                    : headers.getAcceptLanguage().trim().toLowerCase();

            // Default to English if language not supported
            if (!lang.equals("en") && !lang.equals("ar")) {
                log.warn("Unsupported language '{}', defaulting to English.", lang);
                lang = "en";
            }

            log.info("Fetching FAQs for language: {}", lang);

            List<Faq> faqs = faqRepository.findByLangIgnoreCase(lang);

            FaqResponse faqResponse = new FaqResponse(
                    faqs.stream()
                            .map(f -> new FaqDTO(f.getQuestion(), f.getAnswer()))
                            .collect(Collectors.toList())
            );

            if (faqs.isEmpty()) {
                response.setStatus(new ResultUtilVO(AppConstant.NOT_FOUND_CODE, AppConstant.NOT_FOUND_DESC));
            } else {
                response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            }

            response.setData(faqResponse);
            log.info("FAQs retrieved. Total FAQs: {}", faqs.size());

        } catch (Exception e) {
            log.error("Error fetching FAQs", e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }

        return response;
    }


}
