package com.microservice.otpservice.controller;

import com.microservice.otpservice.dto.RegisterCheckDto;
import com.microservice.otpservice.dto.RegisterVerificationDto;
import com.microservice.otpservice.service.OTPService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/otpservice")
public class OTPController {

    private final OTPService otpService;

    @Autowired
    public OTPController(OTPService otpService) {
        this.otpService = otpService;
    }

    @Value("${server.port}")
    private String portserver;

    @PostMapping("/request")
    public ResponseEntity<?> requestOTP(@RequestBody RegisterCheckDto registerCheckDto){
        System.out.printf("diakses" + portserver);
        log.debug("request otp: {}", registerCheckDto);
        otpService.requestOTP(registerCheckDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tesload")
    public ResponseEntity<String> tesLoad(){
        System.out.println("diakses di " + portserver);
        log.debug("Welcome");
        return ResponseEntity.status(HttpStatus.OK).body(portserver);
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verification(@RequestBody RegisterVerificationDto registerVerificationDto){
        return otpService.verificationOTP(registerVerificationDto);
    }
}
