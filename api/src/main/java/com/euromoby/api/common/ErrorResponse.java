package com.euromoby.api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private String context;


    public static ErrorResponse of(ErrorCode errorCode, String context) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage(), context);
    }

    public static Mono<ServerResponse> unauthorized(ErrorCode errorCode, String context) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(of(errorCode, context)), ErrorResponse.class);
    }

    public static Mono<ServerResponse> badRequest(ErrorCode errorCode, String context) {
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(of(errorCode, context)), ErrorResponse.class);
    }

    public static Mono<ServerResponse> notFound(ErrorCode errorCode, String context) {
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(of(errorCode, context)), ErrorResponse.class);
    }

    public static Mono<ServerResponse> conflict(ErrorCode errorCode, String context) {
        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(of(errorCode, context)), ErrorResponse.class);
    }
}
