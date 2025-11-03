package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.I18Service;
import com.digi.common.adapter.repository.I18Repository;
import com.digi.common.adapter.repository.PageConfigRepository;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.repository.ErrorMessagesRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.LabelListRes;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.entity.ErrorMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class I18ServiceImpl implements I18Service {

    @Autowired
    private I18Repository i18Repo;

    @Autowired
    private PageConfigRepository pageConfigRepository;

    @Autowired
    private ErrorMessagesRepository errorMessagesRepository;

    @Override
    public GenericResponse<LabelListRes> labelList(String serviceId, String moduleId, String subModuleId, String screenId,String unit, String channel, String lang) {

        GenericResponse<LabelListRes> response = new GenericResponse<LabelListRes>();
        LabelListRes resData = new LabelListRes();
        Map<String, String> i18Map = new LinkedHashMap<String, String>();
        Map<String, String> validationsMap = new LinkedHashMap<String, String>();
        ResultUtilVO resultVo = null;
        try {
            resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
            var labelList = i18Repo.getLabelList(unit, channel, lang);
            var validationList = pageConfigRepository.getValidations(unit, channel);
            List<ErrorMessages> errorCodeAndMsg = errorMessagesRepository.findByIsActiveTrue();

            if (Objects.nonNull(labelList) && !labelList.isEmpty()) {
                labelList.stream().forEach(label -> {
                    i18Map.put(label.getKey(), label.getValue());

                });
            }

            if(Objects.nonNull(errorCodeAndMsg) && !errorCodeAndMsg.isEmpty()) {
                errorCodeAndMsg.stream().forEach(em->{

                    String messageAr = em.getMessageAr();
                    String messageEn = em.getMessageEn();

                    String Msg="ar".equalsIgnoreCase(lang)?messageAr:messageEn;
                    i18Map.put(em.getErrorCode(), Msg);
                });
            }

            if (Objects.nonNull(validationList) && !validationList.isEmpty()) {
                validationList.stream().forEach(validations -> {
                    validationsMap.put(validations.getKey(), validations.getValue());
                });
            }
            resData.setI18(i18Map);
            resData.setValidations(validationsMap);
            response.setData(resData);
        } catch (Exception e) {
            log.info("Exception in I18N :{}", e);
            resultVo = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }
        response.setStatus(resultVo);
        return response;
    }

}
