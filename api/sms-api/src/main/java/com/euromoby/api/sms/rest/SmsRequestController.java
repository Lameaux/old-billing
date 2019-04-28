package com.euromoby.api.sms.rest;

import com.euromoby.api.sms.dto.SmsRequest;
import com.euromoby.api.sms.dto.SmsRequestStatus;
import com.euromoby.api.sms.exception.MethodNotAllowedException;
import com.euromoby.api.sms.exception.ResourceNotFoundException;
import com.euromoby.api.sms.service.SmsRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = SmsRequestController.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsRequestController {
    static final String BASE_URL = "/api/v1/sms/requests";

    @Autowired
    private SmsRequestService service;

    private SmsRequestResourceAssembler assembler;

    public SmsRequestController() {
        assembler = new SmsRequestResourceAssembler();
    }

    @GetMapping("/{id}")
    public Resource<SmsRequest> one(@PathVariable UUID id) {
        return assembler.toResource(service.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }


    @GetMapping
    public Resources<Resource<SmsRequest>> all() {
        List<Resource<SmsRequest>> smsRequests = service.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(smsRequests, linkTo(methodOn(SmsRequestController.class).all()).withSelfRel());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Resource<SmsRequest>> newRequest(@RequestParam String msisdn, @RequestParam String message) {
        SmsRequest newSmsRequest = SmsRequest.builder().msisdn(msisdn).message(message).build();

        SmsRequest savedSmsRequest = service.save(newSmsRequest);

        return ResponseEntity
                .created(linkTo(methodOn(SmsRequestController.class).one(savedSmsRequest.getId())).toUri())
                .body(assembler.toResource(savedSmsRequest));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ResourceSupport> cancel(@PathVariable UUID id) {
        SmsRequest smsRequest = service.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        smsRequest = service.cancel(smsRequest);

        if (smsRequest.getStatus() != SmsRequestStatus.CANCELLED) {
            throw new MethodNotAllowedException("You can't cancel a request that is in the "
                    + smsRequest.getStatus() + " status");
        }

        return ResponseEntity.ok(assembler.toResource(service.save(smsRequest)));
    }
}
