package com.sobkolex.account_service.exseption;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistException extends ResponseStatusException {

    public UserAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST, "User exist!");
    }
}
