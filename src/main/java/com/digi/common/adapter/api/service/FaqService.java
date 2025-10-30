package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.FaqResponse;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;

public interface FaqService {
    public GenericResponse<FaqResponse> getFaqs(DefaultHeadersDto headers, RequestDto requestDto);
}