package org.example.server.exceptions.job_application;

import lombok.Getter;
import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenApplicationAccess extends ApplicationException {
    public ForbiddenApplicationAccess(String message) {

        super(message, HttpStatus.FORBIDDEN);
    }
}
