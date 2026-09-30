package com.example.shortener.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.exception.BusinessException;
import com.example.shortener.repository.ShortUrlRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ShortUrlServiceTest {
    @Mock
    private ShortUrlRepository repository;

    @InjectMocks
    private ShortUrlService service;

    @Test
    public void createsUrlWithRequestedAlias() {
        System.setProperty("shortener.base-url", "http://gld.at/");
        when(repository.findByShortCode("manual")).thenReturn(Optional.<ShortUrl>empty());
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://example.com/article");
        request.setCustomAlias("manual");

        ShortUrlResponse response = service.create(request);

        assertEquals("manual", response.getShortCode());
        assertEquals("http://gld.at/manual", response.getShortUrl());
        assertTrue(response.getCustomAlias());
        assertEquals(Long.valueOf(0L), response.getTotalClicks());
        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(repository).save(captor.capture());
        assertEquals("https://example.com/article", captor.getValue().getOriginalUrl());
    }

    @Test
    public void generatesSixCharacterBase62Code() {
        when(repository.findByShortCode(anyString())).thenReturn(Optional.<ShortUrl>empty());
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://example.com/article");

        ShortUrlResponse response = service.create(request);

        assertTrue(response.getShortCode().matches("[a-zA-Z0-9]{6}"));
        assertEquals("http://gld.at/" + response.getShortCode(), response.getShortUrl());
    }

    @Test
    public void rejectsDuplicateAlias() {
        ShortUrl existing = new ShortUrl();
        when(repository.findByShortCode("manual")).thenReturn(Optional.of(existing));
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://example.com");
        request.setCustomAlias("manual");

        try {
            service.create(request);
        } catch (BusinessException exception) {
            assertEquals(409, exception.getStatus());
            return;
        }
        throw new AssertionError("Expected duplicate alias to be rejected");
    }

    @Test
    public void rejectsNonHttpUrls() {
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("ftp://example.com/file");

        try {
            service.create(request);
        } catch (BusinessException exception) {
            assertEquals(400, exception.getStatus());
            return;
        }
        throw new AssertionError("Expected invalid URL to be rejected");
    }

    @Test
    public void rejectsExpiredShortUrl() {
        ShortUrl expired = new ShortUrl();
        expired.setShortCode("manual");
        expired.setOriginalUrl("https://example.com");
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(repository.findByShortCode("manual")).thenReturn(Optional.of(expired));

        try {
            service.getRedirectUrl("manual");
        } catch (BusinessException exception) {
            assertEquals(410, exception.getStatus());
            return;
        }
        throw new AssertionError("Expected expired URL to be rejected");
    }
}