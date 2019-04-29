package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserProfile;
import com.euromoby.api.user.exception.ResourceNotFoundException;
import com.euromoby.api.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = UserProfileController.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UserProfileController {
    static final String BASE_URL = "/api/v1/user/profiles";

    @Autowired
    private UserProfileService service;

    private UserProfileResourceAssembler assembler = new UserProfileResourceAssembler();

    @GetMapping("/{id}")
    public Resource<UserProfile> one(@PathVariable UUID id) {
        return assembler.toResource(service.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Resource<UserProfile>> createUserProfile(@Valid @Email @RequestParam String email,
                                                                   @Valid @Size(min = 6) @RequestParam String password) {
        UserProfile userProfile = service.create(email, password);

        return ResponseEntity
                .created(linkTo(methodOn(UserProfileController.class).one(userProfile.getId())).toUri())
                .body(assembler.toResource(userProfile));
    }

}
