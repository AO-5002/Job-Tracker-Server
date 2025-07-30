package org.example.server.exceptions.job_application;

import lombok.Getter;
import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationAlreadyExists extends ApplicationException {
    public ApplicationAlreadyExists(String message) {

        super(message, HttpStatus.CONFLICT);
    }
}
