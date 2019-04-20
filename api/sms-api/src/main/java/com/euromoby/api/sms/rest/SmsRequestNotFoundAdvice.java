package com.euromoby.api.sms.rest;

import com.euromoby.api.sms.exception.SmsRequestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SmsRequestNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(SmsRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String employeeNotFoundHandler(SmsRequestNotFoundException ex) {
        return ex.getMessage();
    }
}
