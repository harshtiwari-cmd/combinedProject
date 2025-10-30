package com.digi.common.adapter.api.service;

import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ProductMasterDto;

import java.util.List;
import java.util.Map;

public interface MenuService {

    GenericResponse<List<ProductMasterDto>> getMenus(String unit, String channel, String lang, String serviceId, String moduleId, String subModuleId, String screenId, Map<String, Object> request);

    GenericResponse<Map<String, Object>> serviceEntitlement(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId);
}
