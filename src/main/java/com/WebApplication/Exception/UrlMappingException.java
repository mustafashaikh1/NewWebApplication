package com.WebApplication.Exception;

public class UrlMappingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UrlMappingException(String message) {
        super(message);
    }

    public UrlMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrlMappingException(Throwable cause) {
        super(cause);
    }

    public UrlMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}