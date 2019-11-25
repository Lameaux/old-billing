package com.euromoby.api.user_api.rest;

import com.euromoby.api.user_api.model.User;
import com.euromoby.api.user_api.repo.UserRepository;
import com.euromoby.api.user_api.rest.dto.UserRequest;
import com.euromoby.api.user_api.rest.dto.UserResponse;
import com.euromoby.api.user_api.rest.exceptions.UserNotFoundException;
import com.euromoby.api.user_api.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserRestController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        return ResponseEntity.ok().allow(
                HttpMethod.GET, HttpMethod.POST,
                HttpMethod.PUT, HttpMethod.DELETE,
                HttpMethod.OPTIONS
        ).build();
    }

    @GetMapping
    public ResponseEntity<Collection<UserResponse>> all() {
        return ResponseEntity.ok(userRepository.findAll()
                .stream().map(UserResponse::fromUser)
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable String id) {
        return userRepository.findById(id).map(
                user -> ResponseEntity.ok(UserResponse.fromUser(user))
        ).orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> post(@RequestBody UserRequest userRequest) {
        User user = userService.create(userRequest);
        UserResponse userResponse = UserResponse.fromUser(user);
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}").buildAndExpand(userResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(userResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponse> put(@PathVariable String id, @RequestBody UserRequest userRequest) {
        return userService.update(id, userRequest).map(
                user -> {
                    UserResponse userResponse = UserResponse.fromUser(user);
                    URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                    return ResponseEntity.created(selfLink).body(userResponse);
                }
        ).orElseThrow(() -> new UserNotFoundException(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return userService.delete(id).map(user -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
