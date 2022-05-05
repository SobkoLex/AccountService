package com.sobkolex.account_service.exseption;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CantRemoveAdminException extends ResponseStatusException {

    public CantRemoveAdminException() {
        super(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
    }
}
