package com.euromoby.payment.rest;

import com.euromoby.payment.entity.Merchant;
import com.euromoby.payment.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
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
    static final String HEADER_MERCHANT = "x-euromoby-merchant";
    static final String HEADER_SECRET = "x-euromoby-secret";

    static final Mono<ServerResponse> UNAUTHORIZED = ServerResponse.status(HttpStatus.UNAUTHORIZED).build();

    private MerchantRepository merchantRepository;

    public AuthFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        String merchant = request.headers().firstHeader(HEADER_MERCHANT);
        String secret = request.headers().firstHeader(HEADER_SECRET);

        if (StringUtils.isEmpty(merchant) || StringUtils.isEmpty(secret)) {
            return UNAUTHORIZED;
        }


        Mono<Merchant> merchantMono = merchantRepository.findById(UUID.fromString(merchant));

        Predicate<Merchant> validateMerchant = m -> m.isActive() && BCrypt.checkpw(secret, m.getSecret());

        return merchantMono.filter(validateMerchant).flatMap(m -> next.handle(request)).switchIfEmpty(UNAUTHORIZED);
    }
}
