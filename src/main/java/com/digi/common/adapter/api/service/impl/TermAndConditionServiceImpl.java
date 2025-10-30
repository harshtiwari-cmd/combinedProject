package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.TermAndConditionService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.repository.TermsAndConditionRepo;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.dto.TermsAndConditionDto;
import com.digi.common.entity.TermsAndCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TermAndConditionServiceImpl implements TermAndConditionService {

	@Autowired
 	private TermsAndConditionRepo termsRepo;

	@Override
	public GenericResponse<List<TermsAndConditionDto>> getTermsAndConditions(String unit, String channel, String lang, String serviceId, String screenId) {

		GenericResponse<List<TermsAndConditionDto>> responeObj = new GenericResponse<>();
		List<TermsAndConditionDto> dtoUrl = new ArrayList<>();
		ResultUtilVO resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
		try {
			log.info("before calling getTermsAndCondition config method lang :{}, screenId : {}", lang,  screenId);
			List<TermsAndCondition> result = termsRepo.findTermsAndconditionBasedOnLangAndScreenId(lang, screenId);
			log.info("after calling getTermsAndCondition config method : {}");
			for (TermsAndCondition obj : result) {
				TermsAndConditionDto dto = new TermsAndConditionDto();
				dto.setUrl(obj.getTcUrl());
				dto.setUrlId(obj.getTcUrlId());
				dto.setScreenId(obj.getScreenId());
				dto.setLabel(obj.getSubModId());
				dtoUrl.add(dto);
				responeObj.setData(dtoUrl);
			}
		} catch (Exception e) {
			log.info("error while calling getTermsAndCondition method : {}", e);
			resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
		}
		responeObj.setStatus(resultVo);
		return responeObj;
	}

}
