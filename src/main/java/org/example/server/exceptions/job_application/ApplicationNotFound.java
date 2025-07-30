package org.example.server.exceptions.job_application;

import lombok.Getter;
import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationNotFound extends ApplicationException {
    public ApplicationNotFound(String message) {

        super(message, HttpStatus.NOT_FOUND);
    }
}
