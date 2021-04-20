package com.euromoby.api.merchant;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.AuthenticationUtil;
import com.euromoby.api.security.IsUser;
import com.euromoby.api.security.UserAuthentication;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MerchantHandler {
    private static final String PARAM_ID = "id";
    private static final String PARAM_NAME = "name";

    private final MerchantService merchantService;

    public MerchantHandler(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @IsUser
    Mono<ServerResponse> listMerchants(ServerRequest serverRequest) {
        return serverRequest.principal().flatMap(principal -> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;

            if (AuthenticationUtil.isAdmin(userAuthentication)) {
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(merchantService.getAllMerchants(), MerchantResponse.class);
            }

            UUID userId = (UUID) userAuthentication.getPrincipal();

            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(merchantService.getAllMerchantsForUserId(userId), MerchantResponse.class);
        });
    }

    @IsUser
    Mono<ServerResponse> getMerchant(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }
        UUID merchantId = UUID.fromString(id);

        return serverRequest.principal().flatMap(principal -> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;

            if (AuthenticationUtil.isAdmin(userAuthentication)) {
                Mono<MerchantResponse> userResponseMono = merchantService.getMerchant(UUID.fromString(id));

                return userResponseMono.flatMap(u -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(u)
                ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "merchant"));
            }

            UUID userId = (UUID) userAuthentication.getPrincipal();

            Mono<MerchantResponse> userResponseMono = merchantService.getMerchantForUserId(userId, merchantId);

            return userResponseMono.flatMap(u -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(u)
            ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "merchant"));

        });
    }

    @IsUser
    Mono<ServerResponse> createMerchant(ServerRequest serverRequest) {
        Mono<MerchantRequest> merchantRequestMono = serverRequest.bodyToMono(MerchantRequest.class);

        return serverRequest.principal().flatMap(principal -> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;
            UUID userId = (UUID) userAuthentication.getPrincipal();

            Mono<MerchantResponse> merchantResponseMono = merchantService.createMerchant(merchantRequestMono, userId);

            return merchantResponseMono.flatMap(u -> ServerResponse.created(null)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(u)
            ).onErrorResume(
                    DuplicateKeyException.class,
                    throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_NAME)
            );
        });
    }
}
