package com.digi.common.adapter.api.controller;


import com.digi.common.adapter.api.service.FaqService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.FaqResponse;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/faq")
public class FaqController {

    @Autowired
    private  FaqService faqService;

    @PostMapping("/view-faqs")
    public ResponseEntity<GenericResponse<FaqResponse>> getFaqs(
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage,
            @RequestBody RequestDto requestBody
    ) {
        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );

        return ResponseEntity.ok(faqService.getFaqs(headers, requestBody));
    }

}
 