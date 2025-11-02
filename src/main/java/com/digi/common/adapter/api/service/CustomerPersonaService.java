package com.digi.common.adapter.api.service;
 
import java.util.List;

import com.digi.common.domain.model.dto.PersonaResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;

 
 
public interface CustomerPersonaService {
	public GenericResponse<List<PersonaResponseDto>> getAllThePersonas(RequestDto dto);

 
}