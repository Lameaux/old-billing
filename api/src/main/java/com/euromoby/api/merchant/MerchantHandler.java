package com.euromoby.api.merchant;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.IsOwner;
import com.euromoby.api.security.IsUser;
import com.euromoby.api.user.UserRole;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Mono<? extends Principal> principalMono = serverRequest.principal();

        return principalMono.flatMap(principal -> {
            Authentication authentication = (Authentication) principal;

            Set<UserRole> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());

            if (roles.contains(UserRole.ROLE_ADMIN)) {
                return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(merchantService.getAllMerchants(), MerchantResponse.class);
            }

            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            merchantService.getAllMerchantsByUserId((UUID) authentication.getPrincipal()),
                            MerchantResponse.class
                    );

        });
    }

    @IsOwner
    Mono<ServerResponse> getMerchant(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }

        Mono<MerchantResponse> userResponseMono = merchantService.getMerchant(UUID.fromString(id));

        return userResponseMono.flatMap(u -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "merchant"));
    }

    @IsUser
    Mono<ServerResponse> createMerchant(ServerRequest serverRequest) {
        Mono<MerchantRequest> merchantRequestMono = serverRequest.bodyToMono(MerchantRequest.class);

        Mono<MerchantResponse> merchantResponseMono = merchantService.createMerchant(merchantRequestMono);

        return merchantResponseMono.flatMap(u -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
        ).onErrorResume(
                DuplicateKeyException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_NAME)
        );
    }
}
