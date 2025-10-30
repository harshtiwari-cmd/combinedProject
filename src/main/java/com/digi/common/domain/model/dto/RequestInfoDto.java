package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfoDto {
    //Otp Properties
    private String otpCode;
    private String qid;
    private String mobileNumber;
    private String emailAddress;



    //Constructor for Otp
    public RequestInfoDto (String otpCode, String qid, String mobileNumber, String emailAddress) {
        this.otpCode = otpCode;
        this.qid = qid;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
    }

    //Product Properties
    private String productType;
    private Long id;

    //Constructor For Product
    public RequestInfoDto(String productType, Long id){
        this.productType = productType;
        this.id = id;
    }

    //Constructor For Product
    public RequestInfoDto( Long id){
        this.id = id;
    }

    //    User Properties
    private String type;

//    User Constructor

    public RequestInfoDto(String type) {
        this.type=type;
    }




}