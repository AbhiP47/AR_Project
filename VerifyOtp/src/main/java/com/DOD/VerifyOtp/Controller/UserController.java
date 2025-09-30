package com.DOD.VerifyOtp.Controller;

import com.DOD.VerifyOtp.Entity.User;
import com.DOD.VerifyOtp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required.");
        }

        if (userService.isUserExistsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        try {
            User newUser = new User();
            newUser.setUsername(username);
            // Hash the password before saving to the database for security
            String hashedPassword = passwordEncoder.encode(password);
            // Print the hashed password to the console for verification
            System.out.println("Hashed password on registration: " + hashedPassword);
            newUser.setPassword(hashedPassword);

            // Save the new user record
            userService.registerNewUser(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            // This line will print the full exception details
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile() {
        String subject = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok("Welcome, your authenticated subject is: " + subject);
    }
}