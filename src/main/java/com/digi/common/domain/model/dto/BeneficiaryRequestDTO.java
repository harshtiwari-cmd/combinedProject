package com.digi.common.domain.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BeneficiaryRequestDTO {
    private String customerId;
    private String beneficiaryAccountNo;
    private String beneficiaryAccountType;
    private String beneficiaryName;
    private String beneficiaryType;
    private String beneficiaryAddress;
    private String bankName;
    private String bankAddress;
    private String bankCity;
    private String bankCountry;
    private String beneficiaryStatus;
    private String bankBic;
    private String nickname;
    private String activationRefNo;
    private String avatarImageUrl;
    private String mobileNumber;
    private Boolean isContactBased;
    private String transferTypeTag;

    private Boolean isFavorite;
    private LocalDateTime lastTransactionDate;
    private Boolean deleted;
}
