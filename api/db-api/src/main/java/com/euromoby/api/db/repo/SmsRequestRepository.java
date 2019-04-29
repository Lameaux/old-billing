package com.euromoby.api.db.repo;

import com.euromoby.api.db.entity.SmsRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "sms-requests", path = "sms/requests")
public interface SmsRequestRepository extends PagingAndSortingRepository<SmsRequest, UUID> {
    List<SmsRequest> findByUserId(@Param("userId") UUID userId);
}