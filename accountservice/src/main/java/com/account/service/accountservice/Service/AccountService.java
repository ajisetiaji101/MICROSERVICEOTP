package com.account.service.accountservice.Service;

import com.account.service.accountservice.dto.RegisterCheckDto;
import com.account.service.accountservice.dto.RegisterPasswordDto;
import com.account.service.accountservice.dto.RegisterVerificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


public interface AccountService {

    public ResponseEntity<?> registerCheck(RegisterCheckDto registerCheckDto);

    ResponseEntity<?> tesLoadAja();

    ResponseEntity<?> verification(RegisterVerificationDto registerVerificationDto);

    ResponseEntity<?> registerPassword(RegisterPasswordDto registerPasswordDto);
}
