package com.digi.common.domain.model.dto;

import lombok.Data;

@Data
public class PersonalizationResponse {

	private String dcusername;
	private String emailaddress;
	private String loginattempts;
	private String mobileno;
	private String hometel;
	private String workTel;
	private String address1;
	private String address2;
	private String natid;
	private String maskednatid;
	private String nickName;
	private String profilePicture;
	private String jointuserPicture;
	private String message;
	private String status;
	private String rmProfilePic;
	private String managerCode;
	private String managerName;
	private String teamLeadCode;
	private String teamLeadName;
	private String rmBranchName;
	private String rmEmail;
	private String rmPhone;
	private String userId;
	private String role;
	private String customerNumber;
	private String registrationDate;
}
