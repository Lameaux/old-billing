package com.euromoby.api.security;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Component
public class AuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    public static final String ATTRIBUTE_MERCHANT = "merchant";
    public static final String HEADER_MERCHANT = "x-merchant";
    public static final String HEADER_SECRET = "x-secret";

    private MerchantRepository merchantRepository;

    @Autowired
    public AuthFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public static UUID getMerchantId(ServerRequest serverRequest) {
        return getMerchant(serverRequest).getId();
    }

    public static Merchant getMerchant(ServerRequest serverRequest) {
        return (Merchant) (serverRequest.attribute(ATTRIBUTE_MERCHANT).orElseThrow());
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        String merchantHeader = request.headers().firstHeader(HEADER_MERCHANT);
        if (!StringUtils.hasText(merchantHeader)) {
            return ErrorResponse.unauthorized(ErrorCode.MISSING_HEADER, HEADER_MERCHANT);
        }

        String secretHeader = request.headers().firstHeader(HEADER_SECRET);
        if (!StringUtils.hasText(secretHeader)) {
            return ErrorResponse.unauthorized(ErrorCode.MISSING_HEADER, HEADER_SECRET);
        }

        Mono<Merchant> merchantMono = merchantRepository.findById(UUID.fromString(merchantHeader));

        Predicate<Merchant> validateMerchant = m -> m.isActive() && BCrypt.checkpw(secretHeader, m.getSecret());

        return merchantMono.filter(validateMerchant).flatMap(merchant -> {
            request.attributes().put(ATTRIBUTE_MERCHANT, merchant);
            return next.handle(request);
        }).switchIfEmpty(ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, HEADER_MERCHANT));
    }
}
