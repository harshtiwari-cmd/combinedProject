package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.BeneficiaryService;
import com.digi.common.domain.model.dto.BeneficiaryRequestDTO;
import com.digi.common.domain.model.dto.BeneficiaryResponseDTO;
import com.digi.common.domain.repository.BeneficiaryRepository;
import com.digi.common.infrastructure.persistance.Beneficiary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository repository;

    @Override
    public BeneficiaryResponseDTO addBeneficiary(BeneficiaryRequestDTO request) {

        Beneficiary beneficiary = Beneficiary.builder()
                .customerId(request.getCustomerId())
                .beneficiaryAccountNo(request.getBeneficiaryAccountNo())
                .beneficiaryAccountType(request.getBeneficiaryAccountType())
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryType(request.getBeneficiaryType())
                .beneficiaryAddress(request.getBeneficiaryAddress())
                .bankName(request.getBankName())
                .bankAddress(request.getBankAddress())
                .bankCity(request.getBankCity())
                .bankCountry(request.getBankCountry())
                .beneficiaryStatus(request.getBeneficiaryStatus())
                .bankBic(request.getBankBic())
                .nickname(request.getNickname())
                .activationRefNo(request.getActivationRefNo())
                .avatarImageUrl(request.getAvatarImageUrl())
                .mobileNumber(request.getMobileNumber())
                .isContactBased(request.getIsContactBased())
                .transferTypeTag(request.getTransferTypeTag())
                .isFavorite(request.getIsFavorite())
                .lastTransactionDate(request.getLastTransactionDate())
                .deleted(request.getDeleted())
                .build();

        repository.save(beneficiary);


        return new BeneficiaryResponseDTO(
                beneficiary.getId(),
                "Beneficiary added successfully"
        );
    }
}
