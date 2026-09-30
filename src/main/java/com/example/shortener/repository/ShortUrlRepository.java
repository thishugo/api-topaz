package com.example.shortener.repository;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.domain.ShortUrlClickLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class ShortUrlRepository {
    @PersistenceContext(unitName = "shortenerPU")
    private EntityManager entityManager;

    public Optional<ShortUrl> findByShortCode(String shortCode) {
        TypedQuery<ShortUrl> query = entityManager.createQuery("select u from ShortUrl u where u.shortCode = :shortCode", ShortUrl.class);
        query.setParameter("shortCode", shortCode);
        List<ShortUrl> results = query.getResultList();
        return results.isEmpty() ? Optional.<ShortUrl>empty() : Optional.of(results.get(0));
    }

    public ShortUrl save(ShortUrl shortUrl) {
        entityManager.persist(shortUrl);
        return shortUrl;
    }

    public List<ShortUrl> findRecent() {
        return entityManager.createQuery("select u from ShortUrl u order by u.createdAt desc", ShortUrl.class)
                .setMaxResults(100)
                .getResultList();
    }

    public List<LocalDateTime> findClickTimestamps(String shortCode) {
        return entityManager.createQuery("select c.clickedAt from ShortUrlClickLog c where c.shortUrl.shortCode = :shortCode order by c.clickedAt desc", LocalDateTime.class)
                .setParameter("shortCode", shortCode)
                .getResultList();
    }

    public void registerClick(String shortCode, LocalDateTime clickedAt) {
        Optional<ShortUrl> result = findByShortCode(shortCode);
        if (result.isPresent()) {
            ShortUrl shortUrl = result.get();
            int updated = entityManager.createQuery("update ShortUrl u set u.totalClicks = u.totalClicks + 1 where u.id = :id")
                    .setParameter("id", shortUrl.getId())
                    .executeUpdate();
            if (updated == 1) {
                ShortUrlClickLog clickLog = new ShortUrlClickLog();
                clickLog.setShortUrl(shortUrl);
                clickLog.setClickedAt(clickedAt);
                entityManager.persist(clickLog);
            }
        }
    }
}