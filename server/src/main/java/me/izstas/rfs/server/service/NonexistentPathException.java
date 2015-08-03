package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.izstas.rfs.server.RfsException;

/**
 * This exception is thrown when the path specified by the user doesn't exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonexistentPathException extends RfsException {
    public NonexistentPathException() {
        super("The path does not exist");
    }

    public NonexistentPathException(Throwable cause) {
        super("The path does not exist", cause);
    }

    public NonexistentPathException(String message) {
        super(message);
    }

    public NonexistentPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
