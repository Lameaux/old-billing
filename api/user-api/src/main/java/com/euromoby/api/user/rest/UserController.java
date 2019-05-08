package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserDto;
import com.euromoby.api.user.exception.ResourceNotFoundException;
import com.euromoby.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = UserController.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UserController {
    static final String BASE_URL = "/api/v1/users";

    @Autowired
    private UserService service;

    private UserResourceAssembler assembler = new UserResourceAssembler();

    @GetMapping
    public Resources<Resource<UserDto>> all() {
        List<Resource<UserDto>> users = service.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public Resource<UserDto> one(@PathVariable UUID id) {
        return assembler.toResource(service.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Resource<UserDto>> createUserProfile(@Valid @Email @RequestParam String email,
                                                               @Valid @Size(min = 6) @RequestParam String password) {
        UserDto userProfile = service.create(email, password);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).one(userProfile.getId())).toUri())
                .body(assembler.toResource(userProfile));
    }

}
