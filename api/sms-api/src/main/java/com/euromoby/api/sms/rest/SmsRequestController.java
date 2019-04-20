package com.euromoby.api.sms.rest;

import com.euromoby.api.sms.dto.SmsRequest;
import com.euromoby.api.sms.dto.SmsRequestStatus;
import com.euromoby.api.sms.exception.SmsRequestNotFoundException;
import com.euromoby.api.sms.service.SmsRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api/v1.0/sms/requests", produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsRequestController {
    private SmsRequestService service;
    private SmsRequestResourceAssembler assembler;

    @Autowired
    public SmsRequestController(SmsRequestService service, SmsRequestResourceAssembler assembler) {
        Assert.notNull(service, "SmsRequestService is missing");
        Assert.notNull(assembler, "SmsRequestResourceAssembler is missing");

        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public Resource<SmsRequest> one(@PathVariable UUID id) {
        return assembler.toResource(service.findById(id).orElseThrow(() -> new SmsRequestNotFoundException(id)));
    }

    @GetMapping
    public Resources<Resource<SmsRequest>> all() {
        List<Resource<SmsRequest>> smsRequests = service.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(smsRequests, linkTo(methodOn(SmsRequestController.class).all()).withSelfRel());
    }

    @PostMapping
    public ResponseEntity<Resource<SmsRequest>> newRequest(@RequestBody SmsRequest smsRequest) {
        SmsRequest newSmsRequest = service.save(smsRequest);

        return ResponseEntity
                .created(linkTo(methodOn(SmsRequestController.class).one(newSmsRequest.getId())).toUri())
                .body(assembler.toResource(newSmsRequest));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ResourceSupport> cancel(@PathVariable UUID id) {
        SmsRequest smsRequest = service.findById(id).orElseThrow(() -> new SmsRequestNotFoundException(id));

        smsRequest = service.cancel(smsRequest);

        if (smsRequest.getStatus() == SmsRequestStatus.CANCELLED) {
            return ResponseEntity.ok(assembler.toResource(service.save(smsRequest)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        new VndErrors.VndError(
                                "Method not allowed",
                                "You can't cancel a request that is in the " + smsRequest.getStatus() + " status"
                        )
                );
    }
}
