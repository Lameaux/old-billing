package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserDto;
import com.euromoby.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = SignupController.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class SignupController {
    public static final String BASE_URL ="/signup";

    @Autowired
    private UserService service;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<UserDto> createUserProfile(@Valid @Email @RequestParam String email,
                                                               @Valid @Size(min = 6) @RequestParam String password) {
        UserDto userProfile = service.create(email, password);

        return ResponseEntity.created(linkTo(methodOn(UserController.class).one(userProfile.getId())).toUri())
                .body(userProfile);
    }
}
