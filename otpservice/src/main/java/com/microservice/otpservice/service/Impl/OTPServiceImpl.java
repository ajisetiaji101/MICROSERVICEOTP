package com.microservice.otpservice.service.Impl;

import com.microservice.otpservice.db.entity.TempOTP;
import com.microservice.otpservice.db.repository.TempOTPRepository;
import com.microservice.otpservice.dto.EmailDto;
import com.microservice.otpservice.dto.RegisterCheckDto;
import com.microservice.otpservice.dto.RegisterVerificationDto;
import com.microservice.otpservice.service.OTPService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Log4j2
@Service
public class OTPServiceImpl implements OTPService {

    private final TempOTPRepository tempOTPRepository;

    private final RedisTemplate redisTemplate;

    private final ChannelTopic channelTopic;

    @Autowired
    public OTPServiceImpl(TempOTPRepository tempOTPRepository, RedisTemplate redisTemplate, ChannelTopic channelTopic) {
        this.tempOTPRepository = tempOTPRepository;
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    public void requestOTP(RegisterCheckDto registerCheckDto){
        TempOTP tempOTPByEmail = tempOTPRepository.getFirstByEmail(registerCheckDto.getEmail());
        if(tempOTPByEmail != null) {
            tempOTPRepository.delete(tempOTPByEmail);
        }

        //Generate Random OTP
        String randomOTP = generateOTP();
        System.out.println(randomOTP);
        log.debug("random otp: {}", randomOTP);

        //Save Redis
        TempOTP tempOTP = new TempOTP();
        tempOTP.setEmail(registerCheckDto.getEmail());
        tempOTP.setOtp(randomOTP);
        tempOTPRepository.save(tempOTP);

        //send email
        sendEmail(registerCheckDto.getEmail(), "Kode verifikasi anda adalah " + randomOTP);

    }

    @Override
    public ResponseEntity<?> verificationOTP(RegisterVerificationDto registerVerificationDto) {
        //check by email
        TempOTP tempOTPByEmail =  tempOTPRepository.getFirstByEmail(registerVerificationDto.getEmail());

        if(tempOTPByEmail == null) return ResponseEntity.notFound().build();

        //verification / validasi otp
        if(!tempOTPByEmail.getOtp().equals(registerVerificationDto.getOtp())) return ResponseEntity.unprocessableEntity().build();

        return ResponseEntity.ok().build();
    }

    private void sendEmail(String to, String body){
        log.debug("to : {}, body : {}", to, body);
        EmailDto emailDto = new EmailDto();
        emailDto.setTo(to);
        emailDto.setSubject("Email Verification");
        emailDto.setBody(body);

        redisTemplate.convertAndSend(channelTopic.getTopic(), emailDto);
    }
    private String generateOTP(){
        return new DecimalFormat("1000").format(new Random().nextInt(999));
    }


}
