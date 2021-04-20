package com.euromoby.api.common;

public enum ErrorCode {
    MISSING_HEADER("Missing header"),
    MISSING_QUERY_PARAM("Missing query parameter"),
    INVALID_QUERY_PARAM("Invalid query parameter"),
    MISSING_BODY_PARAM("Missing body parameter"),
    INVALID_BODY_PARAM("Invalid body parameter"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCESS_DENIED("Access denied"),
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
