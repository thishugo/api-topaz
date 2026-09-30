package com.example.shortener.service;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.dto.ShortUrlStatsResponse;
import com.example.shortener.exception.BusinessException;
import com.example.shortener.repository.ShortUrlRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ShortUrlService {
    private static final String CODE_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DEFAULT_BASE_URL = "http://gld.at";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Inject
    private ShortUrlRepository repository;

    @Lock(LockType.WRITE)
    public ShortUrlResponse create(CreateShortUrlRequest request) {
        validateUrl(request.getOriginalUrl());
        LocalDateTime expiresAt = parseExpiration(request.getExpiresAt());
        String alias = request.getCustomAlias();
        boolean customAlias = alias != null && !alias.trim().isEmpty();
        String shortCode = customAlias ? alias.trim() : generateUniqueCode();

        if (customAlias && repository.findByShortCode(shortCode).isPresent()) {
            throw new BusinessException(409, "Conflict", "The requested alias is already in use");
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.getOriginalUrl().trim());
        shortUrl.setShortCode(shortCode);
        shortUrl.setCustomAlias(customAlias);
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiresAt(expiresAt);
        shortUrl.setTotalClicks(0L);
        repository.save(shortUrl);
        return toResponse(shortUrl);
    }

    public String getRedirectUrl(String shortCode) {
        ShortUrl shortUrl = findRequired(shortCode);
        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException(410, "Gone", "The short URL has expired");
        }
        return shortUrl.getOriginalUrl();
    }

    public List<ShortUrlResponse> findRecent() {
        List<ShortUrlResponse> responses = new ArrayList<ShortUrlResponse>();
        for (ShortUrl shortUrl : repository.findRecent()) {
            responses.add(toResponse(shortUrl));
        }
        return responses;
    }

    public ShortUrlStatsResponse getStats(String shortCode) {
        ShortUrl shortUrl = findRequired(shortCode);
        List<String> timestamps = new ArrayList<String>();
        for (LocalDateTime timestamp : repository.findClickTimestamps(shortCode)) {
            timestamps.add(timestamp.toString());
        }
        return new ShortUrlStatsResponse(shortCode, shortUrl.getTotalClicks(), timestamps);
    }

    private ShortUrl findRequired(String shortCode) {
        Optional<ShortUrl> result = repository.findByShortCode(shortCode);
        if (!result.isPresent()) {
            throw new BusinessException(404, "Not Found", "The short URL does not exist");
        }
        return result.get();
    }

    private String generateUniqueCode() {
        String candidate;
        do {
            StringBuilder code = new StringBuilder(CODE_LENGTH);
            for (int index = 0; index < CODE_LENGTH; index++) {
                code.append(CODE_CHARACTERS.charAt(RANDOM.nextInt(CODE_CHARACTERS.length())));
            }
            candidate = code.toString();
        } while (repository.findByShortCode(candidate).isPresent());
        return candidate;
    }

    private void validateUrl(String value) {
        try {
            URI uri = new URI(value.trim());
            String scheme = uri.getScheme();
            if (!uri.isAbsolute() || uri.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new BusinessException(400, "Bad Request", "The URL must be an absolute HTTP or HTTPS URL");
            }
        } catch (URISyntaxException | NullPointerException exception) {
            throw new BusinessException(400, "Bad Request", "The URL is invalid");
        }
    }

    private LocalDateTime parseExpiration(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDateTime expiration = LocalDateTime.parse(value.trim());
            if (!expiration.isAfter(LocalDateTime.now())) {
                throw new BusinessException(400, "Bad Request", "The expiration must be in the future");
            }
            return expiration;
        } catch (DateTimeParseException exception) {
            throw new BusinessException(400, "Bad Request", "The expiration must use ISO-8601 local date-time format");
        }
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        return new ShortUrlResponse(
                shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCustomAlias(),
                shortUrl.getCreatedAt().toString(),
                shortUrl.getExpiresAt() == null ? null : shortUrl.getExpiresAt().toString(),
                shortUrl.getTotalClicks(),
                createShortUrl(shortUrl.getShortCode()));
    }

    private String createShortUrl(String shortCode) {
        String baseUrl = System.getProperty("shortener.base-url");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = System.getenv("SHORTENER_BASE_URL");
        }
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
        }
        baseUrl = baseUrl.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/" + shortCode;
    }
}