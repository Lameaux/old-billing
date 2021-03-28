package com.euromoby.api.common;

import com.euromoby.api.security.AuthFilter;

public enum ErrorCode {
    MISSING_MERCHANT("Missing " + AuthFilter.HEADER_MERCHANT),
    MISSING_SECRET("Missing " + AuthFilter.HEADER_SECRET),
    INVALID_CREDENTIALS("Invalid credentials");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
