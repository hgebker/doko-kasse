package com.hgebk.dokobackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class DuplicateEveningException extends IllegalStateException{
    public DuplicateEveningException(String date) {
        super(String.format(
                "Evening with date %s already exists",
                date
        ));
    }
}
