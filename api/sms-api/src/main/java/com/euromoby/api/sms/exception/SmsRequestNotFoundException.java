package com.euromoby.api.sms.exception;

import java.util.UUID;

public class SmsRequestNotFoundException extends RuntimeException {

    public SmsRequestNotFoundException(UUID id) {
        super("Could not find SmsRequest " + id);
    }
}