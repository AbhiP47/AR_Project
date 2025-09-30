package com.DOD.VerifyOtp.Repository;

import com.DOD.VerifyOtp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
     Optional<User> findByUsername(String username);
     Optional<User> findByPhoneNumber(String phoneNumber);
}
