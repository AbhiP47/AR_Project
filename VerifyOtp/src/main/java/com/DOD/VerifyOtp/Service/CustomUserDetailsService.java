package com.DOD.VerifyOtp.Service;

import com.DOD.VerifyOtp.Entity.User;
import com.DOD.VerifyOtp.Repository.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepository;

    public CustomUserDetailsService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        // First try to find by username
        User user = userRepository.findByUsername(subject)
                .orElse(null);

        // If not found, try to find by phone number
        if (user == null) {
            user = userRepository.findByPhoneNumber(subject)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with subject: " + subject));
        }

        // Return the user data with the correct password and authority
        // This is a simple, non-role-based authority
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        System.out.println("Password from DB for " + subject + ": " + user.getPassword());


        return new org.springframework.security.core.userdetails.User(
                user.getUsername() != null ? user.getUsername() : user.getPhoneNumber(),
                user.getPassword(), // This is the crucial line: it returns the hashed password
                authorities
        );
    }
}