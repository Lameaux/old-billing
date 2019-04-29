package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserProfile;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UserProfileResourceAssembler implements ResourceAssembler<UserProfile, Resource<UserProfile>> {

    @Override
    public Resource<UserProfile> toResource(UserProfile userProfile) {
        return new Resource<>(userProfile, linkTo(methodOn(UserProfileController.class).one(userProfile.getId())).withSelfRel());
    }

}
