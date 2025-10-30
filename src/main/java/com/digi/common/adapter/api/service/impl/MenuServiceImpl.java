package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.MenuService;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.*;
import com.digi.common.entity.UserScreenSectionMapping;
import com.digi.common.helper.JsonFileReaderHelper;
import com.digi.common.repository.*;
import com.digi.common.service.AsyncLogService;
import com.digi.common.service.ErrorConfigRepositoryAdapter;
import com.digi.common.view.ScreenSectionView;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ProductMasterRepository prodMasterRepo;

	@Autowired
	private HttpServletRequest httpRequest;

	@Autowired
	private JPAUserScreenSectionMapping jPAUserScreenSectionMapping;

	@Autowired
	private URLProviderRepo repository;

	@Autowired
	private SubproductRepository subproductRepo;

	@Autowired
	private JsonFileReaderHelper jsonFileReaderHelper;

	@Autowired
	private ErrorConfigRepositoryAdapter errConfig;

	@Autowired
	private JPARRmessageRepository rrMessageRepository;

	@Autowired
	private AsyncLogService service;

	@Override
	public GenericResponse<List<ProductMasterDto>> getMenus(String unit, String channel, String lang, String serviceId, String moduleId,
			String subModuleId, String screenId, Map<String, Object> request) {

		GenericResponse<List<ProductMasterDto>> responeObj = new GenericResponse<>();
		try {
			responeObj.setData(getMenus1(request));
			responeObj.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
		} catch (Exception e) {
			e.printStackTrace();
			responeObj.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
		}
		return responeObj;
	}

	@Override
	public GenericResponse<Map<String, Object>> serviceEntitlement(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {

		return null;
	}

	private List<ProductMasterDto> getMenus1(Map<String, Object> request) {

		Map<String, ProductMasterDto> resultMap = new LinkedHashMap<>();
		List<ProductMasterDto> menusList = new ArrayList<>();
		try {
			List<Object[]> result = prodMasterRepo.getMenus(httpRequest.getHeader("userRoleCode"));

			for (Object[] obj : result) {
				String productCode = (String)obj[1];
				String productDesc = (String)obj[2];
				String prodServiceId = (String)obj[3];
				String subProductCode = (String)obj[4];
				String subProductDesc = (String)obj[5];
				String subServiceId = (String)obj[7];
				String subProductUrl = (String)obj[8];
				String functionCode = (String)obj[9];
				String functionDesc = (String)obj[10];
				String fServiceId = (String)obj[12];
				String pprocdeureName = (String)obj[13];
				String pprocdeureDesc = (String)obj[14];
				String spprocdeureName = (String)obj[15];
				String spprocdeureDesc = (String)obj[16];
				String fprocdeureName = (String)obj[17];
				String fprocdeureDesc = (String)obj[18];
				String productIcon = (String)obj[19];
				String subProductIcon = (String)obj[20];


				if (resultMap.containsKey(productCode)) {
					ProductMasterDto menu = resultMap.get(productCode);
					List<SubProductMasterDto> subProducts = menu.getSubProducts();
					boolean subProductFound = false;

					for (SubProductMasterDto subProduct : subProducts) {
						if (subProduct.getSubProductCode().equals(subProductCode)) {
							var updatedFunctions = new ArrayList<>(subProduct.getFunctions());
							updatedFunctions.add(
									new FunctionMasterDto(functionCode, functionDesc,fServiceId,
											fprocdeureName, fprocdeureDesc, null));

							var newSubProduct = new SubProductMasterDto(
									subProduct.getSubProductCode(),
									subProduct.getSubProductDesc(),
									subProduct.getSubProductUrl(),
									subProduct.getServiceId(),
									subProduct.getProcdeureName() ,
									subProduct.getSubProductDesc() ,
									subProduct.getSubProductIcon(),
									updatedFunctions
							);
							subProducts.remove(subProduct);
							subProducts.add(newSubProduct);
							subProductFound = true;
							break;
						}
					}

					if (!subProductFound) {
						SubProductMasterDto newSubProduct =
								createSubProduct(subProductCode, subProductDesc, subProductUrl,
										subServiceId, functionCode, functionDesc, fServiceId,
										spprocdeureName, spprocdeureDesc,fprocdeureName,fprocdeureDesc, subProductIcon
								);
						subProducts.add(newSubProduct);
					}
				} else {
					ProductMasterDto menu = new ProductMasterDto();
					menu.setProductCode(productCode);
					menu.setProductDesc(productDesc);
					menu.setServiceId(prodServiceId);
					menu.setProcedureName(pprocdeureName);
					menu.setProcedureDesc(pprocdeureDesc);
					menu.setProductIcon(productIcon);

					SubProductMasterDto newSubProduct =
							createSubProduct(subProductCode, subProductDesc, subProductUrl,
									subServiceId, functionCode, functionDesc, fServiceId,
									spprocdeureName, spprocdeureDesc, fprocdeureName, fprocdeureDesc, subProductIcon);

					menu.setSubProducts(new ArrayList<>(List.of(newSubProduct)));
					resultMap.put(productCode, menu);
				}
			}

			menusList = new ArrayList<>(resultMap.values());

			var userScreenSectionList = new ArrayList<ScreenSectionView>();
			List<Map<String, Object>> resList = new ArrayList<>();
			var reqMap = (Map) request.get("userInfo");
			String action = (String) reqMap.get("action");
			userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserScreenSectionMapping.getSectionList((String) reqMap.get("userName"), "menus");
			if (action.equalsIgnoreCase("GET")) {// GET
				if (userScreenSectionList.isEmpty()) {
					userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserScreenSectionMapping.getDefaultSectionList("default_user", "ALL", "menus");
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
				var sectionList = (List<Map>) reqMap.get("sections");

				var dataList = (ArrayList<UserScreenSectionMapping>) jPAUserScreenSectionMapping
						.findByUserNameAndUserRoleAndScreenCode((String) reqMap.get("userName"), (String) reqMap.get("role"), "menus");

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
						userScreenMapping.setScreenCode("menus");
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
					dataList = (ArrayList<UserScreenSectionMapping>) jPAUserScreenSectionMapping
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
					dataList = (ArrayList<UserScreenSectionMapping>) jPAUserScreenSectionMapping
							.saveAll(dataList);
				}

				userScreenSectionList = (ArrayList<ScreenSectionView>) jPAUserScreenSectionMapping
						.getSectionList((String) reqMap.get("userName"), "menus");

				resList = userScreenSectionList.stream().map(product -> {
					Map<String, Object> map = new HashMap<>();
					map.put("section", product.getSectionCode());
					map.put("title", product.getSectionDesc());
					map.put("priority", product.getPriority()); // Keep priority as Integer
					map.put("enabled", Boolean.parseBoolean(product.getIsEnabled())); // Convert String to Boolean
					return map;
				}).collect(Collectors.toList());
			}

			Map<String, Map<String, Object>> sectionMap = resList.stream()
					.collect(Collectors.toMap(
							entry -> (String) entry.get("section"),  // Key = section (matches productCode)
							entry -> entry                           // Value = Full Map (section data)
					));

			Map<String, Object> finalMap = new HashMap<>();
			finalMap.put("customizerList", resList);
			Map<String, Integer> priorityMap = resList.stream()
					.collect(Collectors.toMap(
							entry -> (String) entry.get("section"),
							entry -> (Integer) entry.get("priority")
					));
			menusList.sort(Comparator.comparingInt(menu ->
					priorityMap.getOrDefault(menu.getProductCode(), Integer.MAX_VALUE)));

			// Update menusList with customizer data
			menusList.forEach(menu -> {
				String productCode = menu.getProductCode();
				if (sectionMap.containsKey(productCode)) {
					Map<String, Object> sectionData = sectionMap.get(productCode);
					menu.setSection((String) sectionData.get("section"));
					menu.setTitle((String) sectionData.get("title"));
					menu.setPriority((Integer) sectionData.get("priority"));
					menu.setEnabled((Boolean) sectionData.get("enabled"));
				}
			});

			for (ProductMasterDto product : menusList) {
				if (product.getSubProducts() != null) {
					for (SubProductMasterDto subProduct : product.getSubProducts()) {
						if (subProduct.getSubProductCode() != null && subProduct.getSubProductCode().startsWith("DEFAULT")) {
							product.setSubProducts(null);
							break;
						}
					}
				}
			}

			//customizer logic end
		} catch (Exception e) {
			log.info("Error while calling getMenu method : {}", e);
		}
		return List.copyOf(menusList);
	}

	private SubProductMasterDto createSubProduct(String subProductCode, String subProductDesc,
												 String subProductUrl,String subProdServiceId, String functionCode, String functionDesc,
												 String fServiceId, String spprocdeureName, String spprocdeureDesc,
												 String fprocdeureName, String fprocdeureDesc, String subProductIcon)
	{
		return new SubProductMasterDto(subProductCode, subProductDesc, subProductUrl, subProdServiceId,
				spprocdeureName,spprocdeureDesc, subProductIcon,
				new ArrayList<>(List.of(new FunctionMasterDto(functionCode, functionDesc, fServiceId,
						fprocdeureName, fprocdeureDesc, null))));
	}

}
