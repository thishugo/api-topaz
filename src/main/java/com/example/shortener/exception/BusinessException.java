package com.example.shortener.exception;

public class BusinessException extends RuntimeException {
    private final int status;
    private final String title;

    public BusinessException(int status, String title, String detail) {
        super(detail);
        this.status = status;
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}