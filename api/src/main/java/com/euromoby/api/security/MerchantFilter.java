package com.euromoby.api.security;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class MerchantFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    private static final String ATTRIBUTE_MERCHANT = "merchant";

    private MerchantRepository merchantRepository;

    @Autowired
    public MerchantFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public static UUID getMerchantId(ServerRequest serverRequest) {
        return getMerchant(serverRequest).getId();
    }

    private static Merchant getMerchant(ServerRequest serverRequest) {
        return (Merchant) (serverRequest.attribute(ATTRIBUTE_MERCHANT).orElseThrow());
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        String merchantName = request.headers().firstHeader(SecurityConstants.HEADER_MERCHANT);
        if (!StringUtils.hasText(merchantName)) {
            return next.handle(request);
        }

        Mono<Merchant> merchantMono = merchantRepository.findByName(merchantName);

        return merchantMono.flatMap(merchant -> {
            request.attributes().put(ATTRIBUTE_MERCHANT, merchant);
            return next.handle(request);
        }).switchIfEmpty(ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, SecurityConstants.HEADER_MERCHANT));
    }
}
