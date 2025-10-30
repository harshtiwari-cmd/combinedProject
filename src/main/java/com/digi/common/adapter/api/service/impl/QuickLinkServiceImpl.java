package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.QuickLinkService;
import com.digi.common.adapter.repository.UserQuickLinkRepository;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.entity.UserQuickLink;
import com.digi.common.view.QuickLinkView;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuickLinkServiceImpl implements QuickLinkService {

    @Autowired
    private UserQuickLinkRepository userQuickLinkRepository;

    @Autowired
    private HttpServletRequest httpRequest;

    @Override
    public GenericResponse<Map<String, Object>> quickLinkList(String unit, String channel, String lang,
                                                              String serviceId, String screenId, String moduleId, String subModuleId, Map<String, Object> requestBody) {

        ResultUtilVO resultVo = new ResultUtilVO();
        GenericResponse<Map<String, Object>> respone = new GenericResponse<>();
        Map<String, Object> responseMap = new HashMap<>();
        List<Map<String, Object>> resLst = new ArrayList<>();
        List<QuickLinkView> quickLinkLst = new ArrayList<>();
        try {
            resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_CODE);

            quickLinkLst = userQuickLinkRepository.findListByUserName(httpRequest.getHeader(AppConstants.USERNAME));
            quickLinkLst = (Objects.nonNull(quickLinkLst) && !quickLinkLst.isEmpty()) ? quickLinkLst : userQuickLinkRepository.findListByUserName("default");
            Map<String, List<QuickLinkView>> listMap = quickLinkLst.stream()
                    .collect(Collectors.groupingBy(
                            QuickLinkView::getProductCode)
                    );

            listMap.forEach((key, value) -> {
                LinkedHashMap<String, Object> resMap = new LinkedHashMap<>();
                resMap.put("productCode", key);
                QuickLinkView quickLinkDet = value.get(0);
                resMap.put("productDesc", quickLinkDet.getProductDesc());
                List<QuickLinkView> subPro = (List<QuickLinkView>) value;
                List<Map<String, String>> subProdLst = new ArrayList<>();
                subPro.forEach(data -> {
                    Map<String, String> subProdMap = new HashMap<>();
                    subProdMap.put("subProductCode", data.getSubProductCode());
                    subProdMap.put("subProductDesc", data.getSubProductDesc());
                    subProdMap.put("icon", data.getIcon());
                    subProdLst.add(subProdMap);
                });
                resMap.put("subProduct", subProdLst);
                resLst.add(resMap);
            });
            responseMap.put("quickLinkList", resLst);
            respone.setData(responseMap);
        } catch (Exception e) {
            log.info("error while calling getTermsAndCondition method : {}", e);
            resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        }
        respone.setStatus(resultVo);
        return respone;
    }

    @Override
    public GenericResponse<Map<String, Object>> quickLinkSave(String unit, String channel, String lang,
                                                              String serviceId, String screenId, String moduleId, String subModuleId, Map<String, Object> requestBody) {

        ResultUtilVO resultVo = new ResultUtilVO();
        GenericResponse<Map<String, Object>> respone = new GenericResponse<>();
        List<UserQuickLink> valuesToInsert = new ArrayList<>();
        List<String> prodDelLst = new ArrayList<>();
        List<String> subProdDelLst = new ArrayList<>();

        try {
            resultVo = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
            List<Map<String, Object>> reqLst = (List<Map<String, Object>>) requestBody.get("quickLinkDet");
            List<QuickLinkView> quickLinkLst = userQuickLinkRepository.findListByUserName(httpRequest.getHeader(AppConstants.USERNAME));
            Map<String, List<QuickLinkView>> listMap = quickLinkLst.stream()
                    .collect(Collectors.groupingBy(
                            QuickLinkView::getProductCode)
                    );
            reqLst.forEach(req -> {
                if ("ADD".equals(req.get("action"))) {
                    req.entrySet().stream()
                            .filter(entry -> !entry.getKey().equals("action"))
                            .forEach(entry -> {
                                List<String> subProdLst = (List<String>) entry.getValue();
                                subProdLst.forEach(subProd -> {
                                    boolean flag = true;
                                    if (Objects.nonNull(listMap.get(entry.getKey().toString()))) {
                                        List<QuickLinkView> existedData = (List<QuickLinkView>) listMap.get(entry.getKey().toString());
                                        if (!existedData.isEmpty() && existedData.stream()
                                                .anyMatch(q -> subProd.equals(q.getSubProductCode()))) {
                                            flag = false;
                                        }
                                    }
                                    if (flag) {
                                        UserQuickLink quickLink = new UserQuickLink();
                                        quickLink.setUserName(httpRequest.getHeader(AppConstants.USERNAME));
                                        quickLink.setProductCode(entry.getKey().toString());
                                        quickLink.setSubProductCode(subProd);
                                        quickLink.setCreatedBy(httpRequest.getHeader(AppConstants.USERNAME));
                                        quickLink.setDateCreated(LocalDateTime.now());
                                        valuesToInsert.add(quickLink);
                                    }
                                });

                            });
                } else {
                    req.entrySet().stream()
                            .filter(entry -> !entry.getKey().equals("action"))
                            .forEach(entry -> {
                                prodDelLst.add(entry.getKey().toString());
                                List<String> subProdLst = (List<String>) entry.getValue();
                                subProdLst.forEach(subProd -> {
                                    subProdDelLst.add(subProd);
                                });

                            });
                }
            });
            if (Objects.nonNull(valuesToInsert) && !valuesToInsert.isEmpty())
                userQuickLinkRepository.saveAll(valuesToInsert);

            if (Objects.nonNull(prodDelLst) && !prodDelLst.isEmpty() && Objects.nonNull(subProdDelLst) && !subProdDelLst.isEmpty())
                userQuickLinkRepository.deleteAll(prodDelLst, subProdDelLst, httpRequest.getHeader(AppConstants.USERNAME));

        } catch (Exception e) {
            log.info("error while calling getTermsAndCondition method : {}", e);
            resultVo = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }
        respone.setStatus(resultVo);
        return respone;
    }
}