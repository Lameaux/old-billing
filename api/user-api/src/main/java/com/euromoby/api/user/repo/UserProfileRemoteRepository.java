package com.euromoby.api.user.repo;

import com.euromoby.api.user.dto.UserProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserProfileRemoteRepository {
    private final ConcurrentHashMap<UUID, UserProfile> map = new ConcurrentHashMap<>();

    public UserProfile save(UserProfile entity) {
        UserProfile savedUserProfile = entity.toBuilder()
                .createdAt(Optional.ofNullable(entity.getCreatedAt()).orElse(LocalDateTime.now()))
                .updatedAt(LocalDateTime.now())
                .build();
        map.put(savedUserProfile.getId(), savedUserProfile);
        return savedUserProfile;
    }

    public void delete(UserProfile entity) {
        map.remove(entity.getId());
    }

    public Optional<UserProfile> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<UserProfile> findAll() {
        return new ArrayList<>(map.values());
    }
}
