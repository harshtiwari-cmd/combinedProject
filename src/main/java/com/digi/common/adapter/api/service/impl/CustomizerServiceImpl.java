package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.CustomizerService;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.entity.UserScreenSectionMapping;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.repository.JPAUserScreenSectionMapping;

import com.digi.common.view.ScreenSectionView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomizerServiceImpl implements CustomizerService {

	@Autowired
	private JPAUserScreenSectionMapping jPAUserQuickProductSubProductRepo;


	@Override
	public GenericResponse<?> customizer(String unit, String channel, String lang, String serviceId, String moduleId,
			String subModuleId, String screenId, Map<String, Object> request) {

		ResultUtilVO resultUtilVO = new ResultUtilVO();
		var response = new GenericResponse<Map>();
		var userScreenSectionList = new ArrayList<ScreenSectionView>();
		List<Map<String, Object>> resList = new ArrayList<>();
		try {
			resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
			var reqMap = (Map) request.get("userInfo");
			String action = (String) reqMap.get("action");
			userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserQuickProductSubProductRepo
					.getSectionList((String) reqMap.get("userName"), "dashboard");
			if (action.equalsIgnoreCase("GET")) {// GET

				if (userScreenSectionList.isEmpty()) {
					userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserQuickProductSubProductRepo
							.getDefaultSectionList(AppConstant.DEFAULT_USER, (String) reqMap.get("role"), "dashboard");
				}
				resList = userScreenSectionList.stream().map(product -> {
					Map<String, Object> map = new HashMap<>();
					map.put("section", product.getSectionCode());
					map.put("title", product.getSectionDesc());
					map.put("priority", product.getPriority()); // Keep priority as Integer
					map.put("enabled", Boolean.parseBoolean(product.getIsEnabled())); // Convert String to Boolean
					return map;
				}).collect(Collectors.toList());

			} else { // MODIFY
				var sectionList = (List<Map>) reqMap.get("dashboard_sections");

				var dataList = (ArrayList<UserScreenSectionMapping>) jPAUserQuickProductSubProductRepo
						.findByUserNameAndUserRoleAndScreenCode((String) reqMap.get("userName"), (String) reqMap.get("role"), "dashboard");

				Map<String, Map<String, Object>> updateMap = sectionList.stream()
						.collect(Collectors.toMap(entry -> (String) entry.get("section"), // Key = section (matches
																							// productCode)
								entry -> Map.of("priority", (Integer) entry.get("priority"), // Value = priority
										"isEnabled", String.valueOf(entry.get("enabled")) // Store "true"/"false" as
																							// String
								)));

				if (dataList.isEmpty()) {
					List<UserScreenSectionMapping> newDataList = new ArrayList<UserScreenSectionMapping>();
					sectionList.forEach(item -> {
						UserScreenSectionMapping userScreenMapping = new UserScreenSectionMapping();
						userScreenMapping.setUserName((String) reqMap.get("userName"));
						userScreenMapping.setUserRole((String) reqMap.get("role"));
						userScreenMapping.setScreenCode("dashboard");
						userScreenMapping.setSectionCode((String) item.get("section"));
						userScreenMapping.setPriority((Integer) item.get("priority"));
						userScreenMapping.setIsEnabled(item.get("enabled").toString());
						userScreenMapping.setCreatedBy("ADMIN");
						userScreenMapping.setCreatedTime(LocalDateTime.now());
						userScreenMapping.setModifiedBy("ADMIN");
						userScreenMapping.setModifiedTime(LocalDateTime.now());
						userScreenMapping.setStatus("ACT");
						newDataList.add(userScreenMapping);
					});
					// Bulk save the updated list
					dataList = (ArrayList<UserScreenSectionMapping>) jPAUserQuickProductSubProductRepo
							.saveAll(newDataList);
				} else {
					// Update fetchedList based on matching productCode
					dataList.forEach(item -> {
						if (updateMap.containsKey(item.getSectionCode())) {
							Map<String, Object> updateValues = updateMap.get(item.getSectionCode());
							item.setPriority((Integer) updateValues.get("priority"));
							item.setIsEnabled((String) updateValues.get("isEnabled"));
						}
					});

					// Bulk save the updated list
					dataList = (ArrayList<UserScreenSectionMapping>) jPAUserQuickProductSubProductRepo
							.saveAll(dataList);
				}

				userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserQuickProductSubProductRepo
						.getSectionList((String) reqMap.get("userName"), "dashboard");

				resList = userScreenSectionList.stream().map(product -> {
					Map<String, Object> map = new HashMap<>();
					map.put("section", product.getSectionCode());
					map.put("title", product.getSectionDesc());
					map.put("priority", product.getPriority()); // Keep priority as Integer
					map.put("enabled", Boolean.parseBoolean(product.getIsEnabled())); // Convert String to Boolean
					return map;
				}).collect(Collectors.toList());
			}

			Map<String, Object> finalMap = new HashMap<>();
			finalMap.put("customizerList", resList);
			response.setData(finalMap);

		} catch (Exception e) {
			log.error("Exception occurred in customizer: {}", e);
			resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
		}
		response.setStatus(resultUtilVO);
		return response;
	}

}
