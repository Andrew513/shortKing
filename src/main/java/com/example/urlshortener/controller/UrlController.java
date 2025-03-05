package com.example.urlshortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.urlshortener.service.UrlService;

import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    /*
    * 	Custom Aliases → Users can define their own short URLs.
	*   Expiration Time → Short links automatically expire.
	*	Click Tracking → Counts total visits.
	*	Analytics Fields → Stores visitor data.
	*	Prepares for QR Codes → //!A future feature.
    */
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, Object> request) {
        try {
            String longUrl = (String) request.get("longUrl");
            String customAlias = (String) request.get("customAlias");
            Integer expiresIn = request.containsKey("expiresIn") ? (Integer) request.get("expiresIn") : null;

            String shortUrl = urlService.generateShortUrl(longUrl, customAlias, expiresIn);
            return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    /*
    
    -H "X-Forwarded-For: 192.168.1.1" \
    -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)" \
    -H "Referer: https://facebook.com" \
    -H "X-Geo-Location: US" 
    
    */
    @GetMapping("/expand/{shortUrl}")
    public ResponseEntity<Map<String, String>> expandUrl(@PathVariable String shortUrl, 
                                                        @RequestHeader(value = "X-Forwarded-For", required = false) String userIp,
                                                        @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                                        @RequestHeader(value = "Referer", required = false) String referrer,
                                                        @RequestHeader(value = "X-Geo-Location", required = false) String location) {

        // Start measuring response time
        Instant startTime = Instant.now();

        // Default values if headers are missing
        userIp = (userIp != null) ? userIp.split(",")[0].trim() : "unknown";
        userAgent = (userAgent != null) ? userAgent : "unknown";
        referrer = (referrer != null) ? referrer : "direct";
        location = (location != null) ? location : "unknown";

        // Convert location to region (Asia, NA, SA, EU, etc.)
        String region = getRegionFromLocation(location);

        Optional<String> longUrl = urlService.getLongUrl("http://short.ly/" + shortUrl, userIp, userAgent, referrer, region);

        if (longUrl.isEmpty()) {
            return ResponseEntity.status(410).body(Map.of("error", "This short URL has expired or does not exist."));
        }

        // Stop measuring response time
        Instant endTime = Instant.now();
        double responseTime = Duration.between(startTime, endTime).toMillis() / 1000.0;

        // Pass response time to service for analytics tracking
        urlService.updateResponseTime(shortUrl, responseTime);

        return ResponseEntity.ok(Map.of("longUrl", longUrl.get()));
    }

    private String getRegionFromLocation(String location) {
        if (location == null || location.isEmpty() || location.equalsIgnoreCase("unknown")) {
            return "Unknown";
        }
    
        // ✅ Convert country names or codes to regions
        String locationLower = location.toLowerCase();
    
        if (locationLower.contains("us") || locationLower.contains("canada") || locationLower.contains("mexico")) {
            return "North America";
        } else if (locationLower.contains("brazil") || locationLower.contains("argentina") || locationLower.contains("peru")) {
            return "South America";
        } else if (locationLower.contains("china") || locationLower.contains("india") || locationLower.contains("japan") || locationLower.contains("korea")) {
            return "Asia";
        } else if (locationLower.contains("germany") || locationLower.contains("france") || locationLower.contains("uk") || locationLower.contains("spain")) {
            return "Europe";
        } else if (locationLower.contains("australia") || locationLower.contains("new zealand")) {
            return "Oceania";
        } else if (locationLower.contains("egypt") || locationLower.contains("south africa") || locationLower.contains("nigeria")) {
            return "Africa";
        }
    
        return "Other";
    }
}