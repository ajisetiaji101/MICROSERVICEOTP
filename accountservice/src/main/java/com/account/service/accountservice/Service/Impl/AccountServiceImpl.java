package com.account.service.accountservice.Service.Impl;

import com.account.service.accountservice.Service.AccountService;
import com.account.service.accountservice.db.entity.Account;
import com.account.service.accountservice.db.entity.TempAccount;
import com.account.service.accountservice.db.repository.AccountRepository;
import com.account.service.accountservice.db.repository.TempAccountRepository;
import com.account.service.accountservice.dto.RegisterCheckDto;
import com.account.service.accountservice.dto.RegisterPasswordDto;
import com.account.service.accountservice.dto.RegisterVerificationDto;
import com.account.service.accountservice.feignclient.OTPClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TempAccountRepository tempAccountRepository;

    @Autowired
    private OTPClient otpClient;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TempAccountRepository tempAccountRepository) {
        this.accountRepository = accountRepository;
        this.tempAccountRepository = tempAccountRepository;
    }

    @Override
    public ResponseEntity<?> registerCheck(RegisterCheckDto registerCheckDto) {

        //check data di postgress
        log.info("Memeriksa akun email di db postgress");
        Account accountbyemail = accountRepository.getFirstByEmail(registerCheckDto.getEmail());

        if(accountbyemail != null) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        //check data di redis
        log.info("Memeriksa akun email di db redis");
        TempAccount redistbyemail = tempAccountRepository.getFirstByEmail(registerCheckDto.getEmail());
        if(redistbyemail != null) return ResponseEntity.ok().build();

        //simpan ke temp redis
        log.info("Menyimpan ke db redis");
        redistbyemail = new TempAccount();
        redistbyemail.setEmail(registerCheckDto.getEmail());
        redistbyemail.setValid(false);
        tempAccountRepository.save(redistbyemail);

        //request OTP
        try {
            log.info("Melakukan register otp");
            otpClient.requestOTP(registerCheckDto);
        }catch (FeignException.FeignClientException ex){
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }



        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<String> tesLoadAja() {

        String hasil;

        try {
            hasil = String.valueOf(otpClient.tesLoad());
        } catch (FeignException.FeignClientException ex) {
            return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
        }

        return ResponseEntity.status(HttpStatus.OK).body(hasil);
    }

    @Override
    public ResponseEntity<?> verification(RegisterVerificationDto registerVerificationDto) {

        //check akun redis
        TempAccount tempAccountEmail = tempAccountRepository.getFirstByEmail(registerVerificationDto.getEmail());

        if(tempAccountEmail ==  null) return ResponseEntity.notFound().build();

        //verifikasi otp


        //update verification
        tempAccountEmail.setValid(true);
        tempAccountRepository.save(tempAccountEmail);

        try {
            otpClient.verificationOTP(registerVerificationDto);
        }catch (FeignException.FeignClientException ex){
            ex.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> registerPassword(RegisterPasswordDto registerPasswordDto) {
        //check akun redis
        TempAccount tempAccount = tempAccountRepository.getFirstByEmail(registerPasswordDto.getEmail());

        if(tempAccount ==  null) return ResponseEntity.notFound().build();

        //verifikasi redis
        if(!tempAccount.isValid()) return ResponseEntity.unprocessableEntity().build();

        //save postgres
        Account account = new Account();
        account.setEmail(registerPasswordDto.getEmail());
        account.setPassword(registerPasswordDto.getPassword());
        try {
            accountRepository.save(account);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

        //delete temp account
        tempAccountRepository.delete(tempAccount);

        return null;
    }
}
