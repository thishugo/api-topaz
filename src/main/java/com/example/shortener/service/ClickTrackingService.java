package com.example.shortener.service;

import com.example.shortener.repository.ShortUrlRepository;
import java.time.LocalDateTime;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class ClickTrackingService {
    @Inject
    private ShortUrlRepository repository;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerClick(String shortCode) {
        repository.registerClick(shortCode, LocalDateTime.now());
    }
}