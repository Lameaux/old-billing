package com.euromoby.api.common;

import com.euromoby.api.security.AuthFilter;

public enum ErrorCode {
    MISSING_HEADER("Missing header"),
    INVALID_CREDENTIALS("Invalid credentials"),
    INVALID_UUID("Not a valid UUID"),
    NOT_FOUND("Record not found");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
