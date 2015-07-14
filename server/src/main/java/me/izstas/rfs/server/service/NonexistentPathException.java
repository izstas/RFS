package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonexistentPathException extends RuntimeException {
    public NonexistentPathException() {
        super("The path does not exist");
    }

    public NonexistentPathException(String message) {
        super(message);
    }

    public NonexistentPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
