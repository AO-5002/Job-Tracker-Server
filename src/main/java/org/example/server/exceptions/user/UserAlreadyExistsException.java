package org.example.server.exceptions.user;

import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApplicationException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
