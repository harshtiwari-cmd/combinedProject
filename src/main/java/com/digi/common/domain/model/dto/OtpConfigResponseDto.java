package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpConfigResponseDto {
    private int otpLength;       // first
    private int otpExpiryTime;   // second
    private int otpRetryCount;   // third
}