package com.example.shortener.dto;

public class ShortUrlResponse {
    private String shortCode;
    private String originalUrl;
    private Boolean customAlias;
    private String createdAt;
    private String expiresAt;
    private Long totalClicks;
    private String shortUrl;

    public ShortUrlResponse() {
    }

    public ShortUrlResponse(String shortCode, String originalUrl, Boolean customAlias, String createdAt, String expiresAt, Long totalClicks, String shortUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.customAlias = customAlias;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.totalClicks = totalClicks;
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Boolean getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(Boolean customAlias) {
        this.customAlias = customAlias;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}