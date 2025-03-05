package com.example.urlshortener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.urlshortener.models.User;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email); // Find a user by email
}