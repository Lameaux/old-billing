package com.euromoby.api.sms.repo;

import com.euromoby.api.sms.dto.SmsRequest;
import com.euromoby.api.sms.dto.SmsRequestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SmsRequestRemoteRepository {
    private final ConcurrentHashMap<UUID, SmsRequest> map = new ConcurrentHashMap<>();

    public SmsRequestRemoteRepository() {
        SmsRequest newSmsRequest = SmsRequest.builder()
                .id(UUID.randomUUID())
                .status(SmsRequestStatus.NEW)
                .msisdn(123456789012345L)
                .message("lorem ipsum dolor")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        map.put(newSmsRequest.getId(), newSmsRequest);
    }

    public SmsRequest save(SmsRequest entity) {
        SmsRequest savedSmsRequest = entity
                .toBuilder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        map.put(savedSmsRequest.getId(), savedSmsRequest);
        return savedSmsRequest;
    }

    public Optional<SmsRequest> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<SmsRequest> findAll() {
        return new ArrayList<>(map.values());
    }
}
