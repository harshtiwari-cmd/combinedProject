package com.digi.common.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rbx_t_beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "beneficiary_account_no", nullable = false)
    private String beneficiaryAccountNo;

    @Column(name = "beneficiary_account_type")
    private String beneficiaryAccountType;

    @Column(name = "beneficiary_name", nullable = false)
    private String beneficiaryName;

    @Column(name = "beneficiary_type", nullable = false)
    private String beneficiaryType;

    @Column(name = "beneficiary_address")
    private String beneficiaryAddress;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_address")
    private String bankAddress;

    @Column(name = "bank_city")
    private String bankCity;

    @Column(name = "bank_country")
    private String bankCountry;

    @Column(name = "beneficiary_status")
    private String beneficiaryStatus;

    @Column(name = "bank_bic")
    private String bankBic;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "activation_ref_no")
    private String activationRefNo;

    @Column(name = "avatar_image_url")
    private String avatarImageUrl;

    @Column(name = "mobile_number")
    private String mobileNumber;

    private Boolean isFavorite = false;
    private Boolean isContactBased = false;
    private String transferTypeTag;
    private LocalDateTime lastTransactionDate;
    private Boolean deleted = false;
}

