package com.example.shortener.dto;

import java.util.List;

public class ShortUrlStatsResponse {
    private String shortCode;
    private Long totalClicks;
    private List<String> clickTimestamps;

    public ShortUrlStatsResponse() {
    }

    public ShortUrlStatsResponse(String shortCode, Long totalClicks, List<String> clickTimestamps) {
        this.shortCode = shortCode;
        this.totalClicks = totalClicks;
        this.clickTimestamps = clickTimestamps;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public List<String> getClickTimestamps() {
        return clickTimestamps;
    }

    public void setClickTimestamps(List<String> clickTimestamps) {
        this.clickTimestamps = clickTimestamps;
    }
}