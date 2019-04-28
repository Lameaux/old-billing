package com.euromoby.api.sms.service;

import com.euromoby.api.sms.dto.SmsRequest;
import com.euromoby.api.sms.dto.SmsRequestStatus;
import com.euromoby.api.sms.repo.SmsRequestRemoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SmsRequestService {
    @Autowired
    private SmsRequestRemoteRepository repository;

    public SmsRequest save(SmsRequest smsRequest) {
        SmsRequest newSmsRequest = smsRequest
                .toBuilder()
                .id(UUID.randomUUID())
                .status(SmsRequestStatus.NEW)
                .build();
        return repository.save(newSmsRequest);
    }

    public SmsRequest cancel(SmsRequest smsRequest) {
        if (smsRequest.getStatus() == SmsRequestStatus.NEW) {
            smsRequest.setStatus(SmsRequestStatus.CANCELLED);
            return repository.save(smsRequest);
        }
        return smsRequest;
    }

    public Optional<SmsRequest> findById(UUID id) {
        return repository.findById(id);
    }

    public List<SmsRequest> findAll() {
        return repository.findAll();
    }
}
