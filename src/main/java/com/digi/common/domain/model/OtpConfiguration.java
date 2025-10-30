package com.digi.common.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_configuration")
public class OtpConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "screen_id", nullable = false, unique = true)
    private Long screenId;

    @Column(name = "otp_expiry_seconds", nullable = false)
    private Integer otpExpirySeconds;

    @Column(name = "otp_max_attempts", nullable = false)
    private Integer otpMaxAttempts;

    @Column(name = "otp_length", nullable = false)
    private Integer otpLength;

    @Column(name = "status")
    private boolean status;  // true = enabled, false = disabled
}
