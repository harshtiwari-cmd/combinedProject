package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.PersonaResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;

import java.util.List;

 
 
public interface CustomerPersonaService {
	public GenericResponse<List<PersonaResponseDto>> getAllThePersonas(RequestDto dto);

 
}