package com.proposal.service;

public interface OtpService {
    String generateOtp(String key);
    boolean validateOtp(String key, String otp);
    void clearOtp(String key);
}
