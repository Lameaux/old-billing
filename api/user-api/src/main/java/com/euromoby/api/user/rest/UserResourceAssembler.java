package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserDto;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UserResourceAssembler implements ResourceAssembler<UserDto, Resource<UserDto>> {

    @Override
    public Resource<UserDto> toResource(UserDto userProfile) {
        return new Resource<>(userProfile, linkTo(methodOn(UserController.class).one(userProfile.getId())).withSelfRel());
    }

}
