package com.hgebk.doko.kasse.earning;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EarningNotFoundException extends NoSuchElementException {
    public EarningNotFoundException(String id) {
        super(String.format("Earning with id '%s' does not exist", id));
    }
}
