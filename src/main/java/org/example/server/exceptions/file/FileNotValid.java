package org.example.server.exceptions.file;

import org.example.server.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

public class FileNotValid extends ApplicationException {
    public FileNotValid(String message) {
      super(message, HttpStatus.BAD_REQUEST);
    }
}
