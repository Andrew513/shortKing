package com.example.urlshortener.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "urls")
public class Url {
    @Id
    private String id;
    private String longUrl;
    private String shortUrl;
    @Indexed(unique = true)
    private String customAlias;
    private int clicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String qrCode;
    private String createdBy;

    // Analytics Data
    private int uniqueVisitors;
    private double avgResponseTime;
    private LocalDateTime lastClicked;
    private Map<String, Integer> deviceStats;
    private Map<String, Integer> locationStats;
    private Map<String, Integer> referrerStats;

    public Url() {
        this.createdAt = LocalDateTime.now();
        this.clicks = 0;
    }

    public Url(String longUrl, String shortUrl, String customAlias, LocalDateTime expiresAt) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.customAlias = customAlias;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.clicks = 0;
    }

    // ✅ Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    public int getClicks() { return clicks; }
    public void setClicks(int clicks) { this.clicks = clicks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public int getUniqueVisitors() { return uniqueVisitors; }
    public void setUniqueVisitors(int uniqueVisitors) { this.uniqueVisitors = uniqueVisitors; }

    public double getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(double avgResponseTime) { this.avgResponseTime = avgResponseTime; }

    public LocalDateTime getLastClicked() { return lastClicked; }
    public void setLastClicked(LocalDateTime lastClicked) { this.lastClicked = lastClicked; }

    public Map<String, Integer> getDeviceStats() { return deviceStats; }
    public void setDeviceStats(Map<String, Integer> deviceStats) { this.deviceStats = deviceStats; }

    public Map<String, Integer> getLocationStats() { return locationStats; }
    public void setLocationStats(Map<String, Integer> locationStats) { this.locationStats = locationStats; }

    public Map<String, Integer> getReferrerStats() { return referrerStats; }
    public void setReferrerStats(Map<String, Integer> referrerStats) { this.referrerStats = referrerStats; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}