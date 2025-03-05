package com.example.urlshortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.urlshortener.models.Url;
import com.example.urlshortener.service.UrlService;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UrlService urlService;

    @GetMapping("/analysis/{userId}")
    public ResponseEntity<Map<String, Object>> getUserUrlAnalysis(@PathVariable String userId) {
        List<Url> userUrls = urlService.getUserUrls(userId);

        if (userUrls.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No URLs found for user " + userId));
        }

        List<Map<String, Object>> urlAnalytics = userUrls.stream().map(url -> Map.of(
                "shortUrl", url.getShortUrl(),
                "longUrl", url.getLongUrl(),
                "clickCount", url.getClicks(),
                "isActive", (url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now())),
                "lastAccessTime", url.getLastClicked(),
                "regionStats", url.getLocationStats(),
                "deviceStats", url.getDeviceStats(),
                "referrerStats", url.getReferrerStats()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "totalUrls", userUrls.size(),
                "urls", urlAnalytics
        ));
    }
}