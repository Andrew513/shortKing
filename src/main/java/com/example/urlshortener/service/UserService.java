package com.example.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.urlshortener.models.User;
import com.example.urlshortener.repository.UserRepository;
import com.example.urlshortener.security.*;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Secure Password Hashing

    

    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(String email, String rawPassword) throws Exception {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("This email is already registered. Try logging in.");
        }

        // Hash password before saving to database
        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(email, hashedPassword);
        return userRepository.save(newUser);
    }

    public String authenticateUser(String email, String rawPassword) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return jwtUtil.generateToken(user.getId(), user.getEmail());
            }
        }
        throw new Exception("Invalid email or password.");
    }
}