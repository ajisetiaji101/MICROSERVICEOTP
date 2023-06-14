package com.account.service.accountservice.feignclient;

import com.account.service.accountservice.dto.RegisterCheckDto;
import com.account.service.accountservice.dto.RegisterVerificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "OTPSERVICE")
public interface OTPClient {

    @PostMapping("/otpservice/request")
    public ResponseEntity<?> requestOTP(@RequestBody RegisterCheckDto registerCheckDto);

    @GetMapping("/otpservice/tesload")
    public ResponseEntity<String> tesLoad();

    @PostMapping("/otpservice/verification")
    public ResponseEntity<?> verificationOTP(RegisterVerificationDto registerVerificationDto);
}