package com.microservice.otpservice.db.repository;

import com.microservice.otpservice.db.entity.TempOTP;
import org.springframework.data.repository.CrudRepository;

public interface TempOTPRepository extends CrudRepository<TempOTP, String> {
    TempOTP getFirstByEmail(String email);
}
