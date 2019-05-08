package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.ErrorDetails;
import com.euromoby.api.user.exception.BadRequestException;
import com.euromoby.api.user.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserControllerAdvice {

    private final Map<Class, HttpStatus> exceptionMapping = new HashMap<>();

    public UserControllerAdvice() {
        exceptionMapping.put(ResourceNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionMapping.put(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        exceptionMapping.put(BadRequestException.class, HttpStatus.BAD_REQUEST);
        exceptionMapping.put(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
        exceptionMapping.put(ConstraintViolationException.class, HttpStatus.BAD_REQUEST);
        exceptionMapping.put(DataIntegrityViolationException.class, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder().timestamp(LocalDateTime.now())
                .message(ex.getMessage()).exception(ex.getClass().getName()).details(request.getDescription(false)).build();

        HttpStatus httpStatus = exceptionMapping.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
