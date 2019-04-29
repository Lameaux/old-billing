package com.euromoby.api.user.service;

import com.euromoby.api.user.dto.UserProfile;
import com.euromoby.api.user.repo.UserProfileRemoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserProfileService {
    @Autowired
    private UserProfileRemoteRepository repository;

    public UserProfile create(String email, String password) {
        UserProfile userProfile = UserProfile.builder().email(email).active(true).build();
        return save(userProfile);
    }

    public UserProfile save(UserProfile userProfile) {
        UserProfile newUserProfile = userProfile
                .toBuilder()
                .id(UUID.randomUUID())
                .build();
        return repository.save(newUserProfile);
    }

    public void delete(UserProfile userProfile) {
        repository.delete(userProfile);
    }

    public Optional<UserProfile> findById(UUID id) {
        return repository.findById(id);
    }

    public List<UserProfile> findAll() {
        return repository.findAll();
    }
}
