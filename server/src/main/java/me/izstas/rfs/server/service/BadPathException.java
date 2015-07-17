package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.izstas.rfs.server.RfsException;

/**
 * @see PathService#validateUserPath(java.nio.file.Path)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadPathException extends RfsException {
    public BadPathException() {
        super("The path is not valid");
    }

    public BadPathException(Throwable cause) {
        super("The path is not valid", cause);
    }

    public BadPathException(String message) {
        super(message);
    }

    public BadPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
