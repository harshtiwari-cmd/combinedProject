package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.UserService;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.dto.GenericResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.digi.common.constants.AppConstants;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserService userService;


    @PostMapping("/rules")
    public GenericResponse<List<RuleDTO>> getRules(@RequestBody RequestDto requestDto,
                                                   @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
                                                   @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
                                                   @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
                                                   @RequestHeader(name = AppConstants.SCREENID) String screenId,
                                                   @RequestHeader(name = AppConstants.CHANNEL) String channel,
                                                   @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = AppConstants.DEFAULT_LANG, required = false) String acceptLanguage) {
        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );

        // Pass type and language to service (service validates language)
        return userService.getRules(
                requestDto.getRequestInfoDto().getType(),
                headers.getAcceptLanguage(),
                requestDto
        );  }
} 