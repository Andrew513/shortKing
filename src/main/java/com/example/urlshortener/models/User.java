package com.example.urlshortener.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users") //  MongoDB Collection Name
public class User {

    @Id
    private String id;  // User ID (MongoDB ObjectId)

    @Indexed(unique = true)
    private String email;  // Unique Email (Ensuring No Duplicates)

    private String password;  // Hashed Password (Store securely!)

    private boolean isActive = true;  // Is the user active?

    private LocalDateTime createdAt = LocalDateTime.now();  // Account creation time

    private String googleId;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}