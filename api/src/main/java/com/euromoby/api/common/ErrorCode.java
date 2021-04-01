package com.euromoby.api.common;

public enum ErrorCode {
    MISSING_HEADER("Missing header"),
    MISSING_QUERY_PARAM("Missing query parameter"),
    INVALID_QUERY_PARAM("Invalid query parameter"),
    INVALID_CREDENTIALS("Invalid credentials"),
    INVALID_UUID("Not a valid UUID"),
    DUPLICATE_VALUE("Value already exists"),
    NOT_FOUND("Record not found");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
