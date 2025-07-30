package org.example.server.exceptions.job_application;

import lombok.Getter;
import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

@Getter
public class NoApplicationsFound extends ApplicationException {
    public NoApplicationsFound(String message) {

        super(message, HttpStatus.NOT_FOUND);
    }
}
