package com.DOD.VerifyOtp.Service;

import com.DOD.VerifyOtp.Entity.User;
import com.DOD.VerifyOtp.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public boolean isUserExistsByPhoneNumber(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber).isPresent();
    }

    public boolean isUserExistsByUsername(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public Optional<User> findUserByPhoneNumber(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User registerNewUser(User user) {
        return userRepo.save(user);
    }

    public boolean validateCredentials(String username, String password) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Compare passwords (consider hashing)
            return user.getPassword().equals(password);
        }
        return false;
    }
}