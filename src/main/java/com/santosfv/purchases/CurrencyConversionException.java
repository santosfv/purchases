package com.santosfv.purchases;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CurrencyConversionException extends RuntimeException {

    public CurrencyConversionException(String message) {
        super(message);
    }
}
