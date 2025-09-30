package com.DOD.VerifyOtp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private TwilioService twilioService;

    private final ConcurrentHashMap<String, String> otpMap = new ConcurrentHashMap<>();

    private static final long OTP_VALIDITY_SECONDS = 300; // 5 minutes

    public void generateAndSendOtp(String phoneNumber) {
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(999999));

        // Store the OTP in the map and schedule its removal after 5 minutes
        otpMap.put(phoneNumber, otp);

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            otpMap.remove(phoneNumber);
        }, OTP_VALIDITY_SECONDS, TimeUnit.SECONDS);

        twilioService.sendOtp(phoneNumber, otp);
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        String storedOtp = otpMap.get(phoneNumber);

        if (storedOtp != null && storedOtp.equals(otp)) {
            // OTP is valid, remove it from the map to prevent reuse
            otpMap.remove(phoneNumber);
            return true;
        }

        return false;
    }
}