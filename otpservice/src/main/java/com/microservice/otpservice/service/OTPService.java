package com.microservice.otpservice.service;

import com.microservice.otpservice.dto.RegisterCheckDto;
import com.microservice.otpservice.dto.RegisterVerificationDto;
import org.springframework.http.ResponseEntity;

public interface OTPService {
    public void requestOTP(RegisterCheckDto registerCheckDto);

    public ResponseEntity<?> verificationOTP(RegisterVerificationDto registerVerificationDto);
}
