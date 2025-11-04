package com.digi.common.adapter.api.service.impl;


import com.digi.common.adapter.api.service.UserServiceClient;
import com.digi.common.domain.model.dto.BankMiddlewareRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.UserLookupResponse;
import com.digi.common.domain.model.dto.UserServiceRequest;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClientImpl.class);

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserLookupResponse getUserByCustomerNumber(String unit, String channel, String acceptLanguage,
                                                      String serviceId, String screenId, String moduleId,
                                                      String subModuleId, String customerNumber, UserServiceRequest userServiceRequest) {
        try {
            String url = userServiceUrl + "/api/users/getUsernameByCustomer";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("unit", unit != null ? unit : AppConstant.DEFAULT_UNIT);
            headers.set("channel", channel != null ? channel : AppConstant.DEFAULT_CHANNEL);
            headers.set("accept-language", acceptLanguage != null ? acceptLanguage : AppConstant.DEFAULT_LANGUAGE);
            headers.set("serviceId", serviceId != null ? serviceId : AppConstant.DEFAULT_SERVICEID);
            headers.set("screenId", screenId != null ? screenId : AppConstant.DEFAULT_SCREENID);
            headers.set("moduleId", moduleId != null ? moduleId : AppConstant.DEFAULT_MODULEID);
            headers.set("subModuleId", subModuleId != null ? subModuleId : AppConstant.DEFAULT_SUNMODULEID);

            HttpEntity<UserServiceRequest> entity = new HttpEntity<>(userServiceRequest, headers);

            ParameterizedTypeReference<GenericResponse<UserLookupResponse>> responseType =
                    new ParameterizedTypeReference<GenericResponse<UserLookupResponse>>() {};

            ResponseEntity<GenericResponse<UserLookupResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            GenericResponse<UserLookupResponse> genericResponse = response.getBody();

            if (genericResponse == null || genericResponse.getData() == null) {
                logger.warn("User service returned empty data for customerNumber: {}", customerNumber);
                return null;
            }

            return genericResponse.getData();

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("User not found in user service for customerNumber: {}", customerNumber);
            return null;
        } catch (Exception e) {
            logger.error("Error calling user service API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call user service API", e);
        }
    }
}


