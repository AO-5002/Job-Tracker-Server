package org.example.server.exceptions.user;

import lombok.Getter;
import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message) {

      super(message, HttpStatus.NOT_FOUND);
    }
}
