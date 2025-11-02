package com.digi.common.infrastructure.common;

import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DeviceInfoDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RequestInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;


public final class HeaderDeviceConstant {


    //---- Header validation

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static List<String> missingMandatoryHeaders(String serviceId,
                                                       String moduleId,
                                                       String subModuleId,
                                                       String screenId,
                                                       String channel) {
        List<String> missing = new ArrayList<>();
        if (isBlank(serviceId)) missing.add(AppConstants.SERVICEID);
        if (isBlank(moduleId)) missing.add(AppConstants.MODULE_ID);
        if (isBlank(subModuleId)) missing.add(AppConstants.SUB_MODULE_ID);
        if (isBlank(screenId)) missing.add(AppConstants.SCREEN_ID);
        if (isBlank(channel)) missing.add(AppConstants.CHANNEL);        // you’re using CHANNEL in the controller
        return missing;
    }

    //------- Device info validation


    public static boolean hasValidDeviceInfo(RequestDto dto) {
        if (dto == null || dto.getDeviceInfo() == null) return false;
        DeviceInfoDto d = dto.getDeviceInfo();

        return nonEmpty(d.getDeviceId())
                && nonEmpty(d.getIpAddress())
                && nonEmpty(d.getOsType())
                && nonEmpty(d.getOsVersion())
                && nonEmpty(d.getAppVersion())
                && nonEmpty(d.getVendorId())
                && nonEmpty(d.getEndToEndId());
    }

    private static boolean nonEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean checkRequestInfoDto(RequestInfoDto requestInfoDto) {

        return requestInfoDto==null;
    }

    public static String mapLanguage(String lang) {

        lang = lang.trim().toLowerCase();

        switch (lang) {
            case "en":
            case "english":
                return "en";
            case "ar":
            case "arabic":
                return "ar";
            default:
                return "en"; // invalid language
        }
    }
}
 