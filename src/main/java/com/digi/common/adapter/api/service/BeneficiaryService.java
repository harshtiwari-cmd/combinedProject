package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.BeneficiaryRequestDTO;
import com.digi.common.domain.model.dto.BeneficiaryResponseDTO;

public interface BeneficiaryService {

    BeneficiaryResponseDTO addBeneficiary(BeneficiaryRequestDTO request);
}
