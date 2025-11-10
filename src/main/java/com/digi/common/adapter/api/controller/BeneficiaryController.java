package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.BeneficiaryService;
import com.digi.common.domain.model.dto.BeneficiaryRequestDTO;
import com.digi.common.domain.model.dto.BeneficiaryResponseDTO;
import com.digi.common.dto.ApiResponse;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.persistance.Beneficiary;
import com.digi.common.infrastructure.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping("/add")
    public ResponseEntity<?> addBeneficiary(@RequestBody BeneficiaryRequestDTO requestDTO) {

        BeneficiaryResponseDTO responseDTO = beneficiaryService.addBeneficiary(requestDTO);

        return ResponseBuilder.success(responseDTO, "Success");
    }

}
