package com.example.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.urlshortener.models.Url;
import org.springframework.dao.DuplicateKeyException;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;

import com.example.urlshortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

//TODO: Missing when logged in, createdBy in the URL db should be assigned
//TODO: Missing endpoint with only loggedin user (jwt and bearer token)

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    private static final String BASE_URL = "http://short.ly/";

    public String generateShortUrl(String longUrl, String customAlias, Integer expiresIn) throws Exception {
        try {
            // Check if the custom alias already exists
            if (customAlias != null && !customAlias.isEmpty()) {
                Optional<Url> aliasCheck = urlRepository.findByShortUrl(BASE_URL + customAlias);
                if (aliasCheck.isPresent()) {
                    throw new Exception("Custom alias '" + customAlias + "' is already taken! Please try another alias.");
                }
            }

            // Generate a new short URL
            String shortUrl = (customAlias != null && !customAlias.isEmpty()) ? 
                              BASE_URL + customAlias : BASE_URL + generateRandomString();

            LocalDateTime expirationTime = (expiresIn != null) ? LocalDateTime.now().plusSeconds(expiresIn) : null;
            

            Url newUrl = new Url(longUrl, shortUrl, customAlias, expirationTime);
            urlRepository.save(newUrl);

            return shortUrl;
        } catch (DuplicateKeyException e) {
            throw new Exception("The alias you selected is already in use. Try another alias.");
        } catch (Exception e) {
            throw new Exception("An unexpected error occurred while generating the short URL. Please try again.");
        }
    }

    public Optional<String> getLongUrl(String shortUrl, String userIp, String userAgent, String referrer, String location) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
    
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
    
            // Check if the URL has expired
            if (url.getExpiresAt() != null && LocalDateTime.now().isAfter(url.getExpiresAt())) {
                urlRepository.delete(url); // Delete expired URL
                return Optional.empty();   // Return empty (indicating expired)
            }
    
            // Start time for response time calculation
            LocalDateTime startTime = LocalDateTime.now();
    
            // Update analytics data
            updateAnalytics(url, userIp, userAgent, referrer, location, startTime);
    
            return Optional.of(url.getLongUrl());
        }
    
        return Optional.empty();
    }

    private String generateRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // private Optional<String> getAuthenticatedUserId() {
    //     try {
    //         Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //         if (principal instanceof UserDetails) {
    //             UserDetails userDetails = (UserDetails) principal;
    //             return Optional.of(userDetails.getUsername()); // Assuming username is the user ID
    //         }
    //     } catch (Exception e) {
    //         return Optional.empty();
    //     }
    //     return Optional.empty();
    // }

    private void updateAnalytics(Url url, String userIp, String userAgent, String referrer, String location, LocalDateTime startTime) {


        if (url.getDeviceStats() == null) {
            url.setDeviceStats(new HashMap<>());
        }
        if (url.getLocationStats() == null) {
            url.setLocationStats(new HashMap<>());
        }
        if (url.getReferrerStats() == null) {
            url.setReferrerStats(new HashMap<>());
        }
    
        // Update unique visitors count
        if (!url.getDeviceStats().containsKey(userIp)) {
            url.setUniqueVisitors(url.getUniqueVisitors() + 1);
        }

        // Update last clicked timestamp
        url.setLastClicked(LocalDateTime.now());
    
        // Track device stats
        Map<String, Integer> deviceStats = url.getDeviceStats();
        String deviceType = getDeviceType(userAgent);
        deviceStats.put(deviceType, deviceStats.getOrDefault(deviceType, 0) + 1);
        url.setDeviceStats(deviceStats);
    
        // Track location stats
        Map<String, Integer> locationStats = url.getLocationStats();
        locationStats.put(location, locationStats.getOrDefault(location, 0) + 1);
        url.setLocationStats(locationStats);
    
        // Track referrer stats
        Map<String, Integer> referrerStats = url.getReferrerStats();
        referrerStats.put(referrer, referrerStats.getOrDefault(referrer, 0) + 1);
        url.setReferrerStats(referrerStats);
    
        // Increment click count
        url.setClicks(url.getClicks() + 1);
    
        // Save updated URL data
        urlRepository.save(url);
    }

    public void updateResponseTime(String shortUrl, double responseTime) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);

        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();

            // Calculate new average response time
            double newAvgResponseTime = ((url.getAvgResponseTime() * url.getClicks()) + responseTime) / (url.getClicks() + 1);
            url.setAvgResponseTime(newAvgResponseTime);

            urlRepository.save(url);
        }
    }

    private String getDeviceType(String userAgent) {
        if (userAgent.toLowerCase().contains("mobile")) {
            return "Mobile";
        } else if (userAgent.toLowerCase().contains("tablet")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    public List<Url> getUserUrls(String userId) {
        return urlRepository.findByCreatedBy(userId);
    }
}