package com.euromoby.api.sms.rest;

import com.euromoby.api.sms.dto.SmsRequest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class SmsRequestResourceAssembler implements ResourceAssembler<SmsRequest, Resource<SmsRequest>> {

    @Override
    public Resource<SmsRequest> toResource(SmsRequest smsRequest) {

        return new Resource<>(smsRequest,
                linkTo(methodOn(SmsRequestController.class).one(smsRequest.getId())).withSelfRel(),
                linkTo(methodOn(SmsRequestController.class).all()).withRel("requests"));
    }

}
