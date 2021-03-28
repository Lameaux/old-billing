package com.euromoby.api.security;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class AuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    public static final String HEADER_MERCHANT = "x-merchant";
    public static final String HEADER_SECRET = "x-secret";

    private MerchantRepository merchantRepository;

    public AuthFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    private Mono<ServerResponse> unauthorizedResponse(ErrorCode errorCode) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(ErrorResponse.of(errorCode)), ErrorResponse.class);
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        String merchant = request.headers().firstHeader(HEADER_MERCHANT);
        if (!StringUtils.hasText(merchant)) {
            return unauthorizedResponse(ErrorCode.MISSING_MERCHANT);
        }

        String secret = request.headers().firstHeader(HEADER_SECRET);
        if (!StringUtils.hasText(secret)) {
            return unauthorizedResponse(ErrorCode.MISSING_SECRET);
        }

        Mono<Merchant> merchantMono = merchantRepository.findById(UUID.fromString(merchant));

        Predicate<Merchant> validateMerchant = m -> m.isActive() && BCrypt.checkpw(secret, m.getSecret());

        return merchantMono.filter(validateMerchant).flatMap(m -> next.handle(request))
                .switchIfEmpty(unauthorizedResponse(ErrorCode.INVALID_CREDENTIALS));
    }
}
