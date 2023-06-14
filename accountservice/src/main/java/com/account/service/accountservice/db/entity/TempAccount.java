package com.account.service.accountservice.db.entity;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "account", timeToLive = 3600)
public class TempAccount {
    @Id
    private String id;
    @Indexed
    private String email;
    private boolean valid = false;
}
