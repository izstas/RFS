package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @see PathService#validateUserPath(java.nio.file.Path)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadPathException extends RuntimeException {
    public BadPathException() {
        super("The path is not valid");
    }
}
