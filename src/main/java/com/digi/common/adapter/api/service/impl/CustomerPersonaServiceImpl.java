package com.digi.common.adapter.api.service.impl;
 
import java.util.Base64;
import java.util.List;

import com.digi.common.adapter.api.service.CustomerPersonaService;
import com.digi.common.domain.model.dto.PersonaResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.repository.PersonaRepository;
import com.digi.common.entity.Personas;
import com.digi.common.infrastructure.common.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;

 
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Service
public class CustomerPersonaServiceImpl implements CustomerPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public GenericResponse<List<PersonaResponseDto>> getAllThePersonas(RequestDto dto) {

        log.info("Entering getAllThePersonas() with RequestDto: {}", dto);

        GenericResponse<List<PersonaResponseDto>> response = new GenericResponse<>();
        try {
            log.debug("Fetching all personas from repository...");
            List<Personas> personas = personaRepository.findAll();
            log.debug("Total personas fetched: {}", personas.size());

            List<PersonaResponseDto> personaResponseDtolist = personas.stream()
                    .map(persona -> {
                        String base64Image = persona.getPersonaImage() != null
                                ? Base64.getEncoder().encodeToString(persona.getPersonaImage())
                                : null;

                        PersonaResponseDto personaResponseDto =
                                new PersonaResponseDto(persona.getPersonaName(), base64Image);
                        log.trace("Mapped persona: {}", personaResponseDto);
                        return personaResponseDto;
                    })
                    .toList();

            response.setStatus(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC));
            response.setData(personaResponseDtolist);

            log.info("getAllThePersonas() executed successfully. Total records: {}", personaResponseDtolist.size());
        } catch (Exception e) {
            log.error("Exception occurred in getAllThePersonas(): {}", e.getMessage(), e);
            response.setStatus(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
            response.setData(null);
        }

        log.debug("Exiting getAllThePersonas() with response: {}", response);
        return response;
    }
}