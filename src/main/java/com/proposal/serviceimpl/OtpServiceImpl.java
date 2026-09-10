package com.proposal.serviceimpl;

import com.proposal.service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private static final long OTP_EXPIRE_SECONDS = 300; // 5 minutes
    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> otpCache = new ConcurrentHashMap<>();

    private static class OtpEntry {
        final String code;
        final Instant expiryTime;

        OtpEntry(String code, Instant expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }

    @Override
    public String generateOtp(String key) {
        String keyClean = key.trim().toLowerCase();
        int number = random.nextInt(1000000);
        String otp = String.format("%06d", number);
        Instant expiry = Instant.now().plusSeconds(OTP_EXPIRE_SECONDS);

        otpCache.put(keyClean, new OtpEntry(otp, expiry));
        return otp;
    }

    @Override
    public boolean validateOtp(String key, String otp) {
        if (key == null || otp == null) {
            return false;
        }
        String keyClean = key.trim().toLowerCase();
        OtpEntry entry = otpCache.get(keyClean);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            otpCache.remove(keyClean);
            return false;
        }
        return entry.code.equals(otp.trim());
    }

    @Override
    public void clearOtp(String key) {
        if (key != null) {
            otpCache.remove(key.trim().toLowerCase());
        }
    }
}
