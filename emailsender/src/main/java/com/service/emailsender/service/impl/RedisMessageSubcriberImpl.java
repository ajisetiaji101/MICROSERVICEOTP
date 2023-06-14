package com.service.emailsender.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.emailsender.dto.EmailDto;
import com.service.emailsender.service.EmailService;
import com.service.emailsender.service.RedisMessageSubcriber;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubcriberImpl implements MessageListener, RedisMessageSubcriber {

    private final EmailService emailService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubcriberImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = message.toString();
        try {
            EmailDto emailDto = objectMapper.readValue(msg, EmailDto.class);
            emailService.sendEmail(emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
