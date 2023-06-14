package com.account.service.accountservice.controller;

import com.account.service.accountservice.Service.AccountService;
import com.account.service.accountservice.dto.RegisterCheckDto;
import com.account.service.accountservice.dto.RegisterPasswordDto;
import com.account.service.accountservice.dto.RegisterVerificationDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/accountservice")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/check")
    public ResponseEntity<?> registerCheck(@RequestBody RegisterCheckDto registerCheckDto){
        log.info("register {}", registerCheckDto);
        return accountService.registerCheck(registerCheckDto);
    }

    @GetMapping("/tesloadaja")
    public ResponseEntity<?> tesloadaja(){
        return accountService.tesLoadAja();
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verification(@RequestBody RegisterVerificationDto registerVerificationDto){
        return accountService.verification(registerVerificationDto);
    }

    @PostMapping("/password")
    public ResponseEntity<?> registerPassword(@RequestBody RegisterPasswordDto registerPasswordDto){
        return accountService.registerPassword(registerPasswordDto);
    }
}
