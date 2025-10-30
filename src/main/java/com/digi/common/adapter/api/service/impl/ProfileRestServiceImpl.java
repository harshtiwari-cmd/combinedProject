package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.ProfileRestService;
import com.digi.common.domain.model.dto.PersonalizationResponse;
import com.digi.common.dto.GenericResponse;
import com.digi.common.helper.JsonFileReaderHelper;
import com.digi.common.repository.URLProviderRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class ProfileRestServiceImpl implements ProfileRestService {

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private URLProviderRepo urlProviderRepo;

    @Autowired
    private JsonFileReaderHelper jsonFileReaderHelper;

    @Autowired
    private URLProviderRepo repository;

    @Override
    public GenericResponse<Object> updateNickNameAndPictureForProfile(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, String nickname, String profilepic) {

        return null;
    }

    @Override
    public GenericResponse<Map<String, PersonalizationResponse>> viewProfile(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId) {


        return null;
    }

    public static String getValidValueElseEmpty(String value) {
        if (value == null || value.isEmpty() || "null".equals(value)) {
            return "";
        } else {
            return value;
        }
    }

    public static String DateFormatter(String input) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        OffsetDateTime dateTime = OffsetDateTime.parse(input, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = dateTime.format(outputFormatter);
        return formattedDate;
    }

    public static String maskQatarId(String input) {
        if (input == null || input.length() < 4) {
            return input;
        }
        String start = input.substring(0, 2);
        String end = input.substring(input.length() - 2);
        int middleLength = input.length() - 4;
        String middle = "*".repeat(middleLength);
        String masked = start + middle + end;
        return masked;
    }
}
