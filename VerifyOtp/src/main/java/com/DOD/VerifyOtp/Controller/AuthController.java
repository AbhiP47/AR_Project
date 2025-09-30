package com.DOD.VerifyOtp.Controller;

import com.DOD.VerifyOtp.Entity.User;
import com.DOD.VerifyOtp.Service.OtpService;
import com.DOD.VerifyOtp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    /**
     * Handle OTP request for sign-up or sign-in
     */
    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null) {
            return ResponseEntity.badRequest().body("Phone number is required.");
        }
        try {
            if (!userService.isUserExistsByPhoneNumber(phoneNumber)) {
                User newUser = new User();
                newUser.setPhoneNumber(phoneNumber);
                userService.registerNewUser(newUser);
            }

            otpService.generateAndSendOtp(phoneNumber);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
        }
    }

    /**
     * Verify OTP - no JWT generation; just confirmation
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        if (phoneNumber == null || otp == null) {
            return ResponseEntity.badRequest().body("Phone number and OTP are required.");
        }

        if (otpService.verifyOtp(phoneNumber, otp)) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
    }

    /**
     * Optional: Simplified login without JWT
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required.");
        }

        if (userService.validateCredentials(username, password)) {
            return ResponseEntity.ok("Login successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }
}
