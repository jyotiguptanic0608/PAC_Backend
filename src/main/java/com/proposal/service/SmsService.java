package com.proposal.service;

public interface SmsService {
    boolean sendOtpSms(String phoneNumber, String otp);
}
