package com.euromoby.api.db.repo;

import com.euromoby.api.db.entity.UserProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "user-profiles", path = "user/profiles")
public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, UUID> {
    List<UserProfile> findByEmail(@Param("email") String email);
}