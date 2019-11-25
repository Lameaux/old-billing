package com.euromoby.api.user_api.rest;

import com.euromoby.api.user_api.rest.exceptions.UserNotFoundException;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@ControllerAdvice(annotations = RestController.class)
public class UserRestControllerAdvice {
    private final MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<VndErrors> notFoundException(UserNotFoundException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getUserId());
    }

    private <E extends Exception> ResponseEntity<VndErrors> error(E error, HttpStatus httpStatus, String logref) {
        String msg = Optional.of(error.getMessage()).orElse(error.getClass().getSimpleName());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(vndErrorMediaType);
        return new ResponseEntity<>(new VndErrors(logref, msg), httpHeaders, httpStatus);
    }
}
