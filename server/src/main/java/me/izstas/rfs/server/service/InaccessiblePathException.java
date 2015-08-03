package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.izstas.rfs.server.RfsException;

/**
 * This exception is thrown when the user doesn't have access to perform the requested operation on the given path.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InaccessiblePathException extends RfsException {
    public InaccessiblePathException() {
        super("The path is not accessible");
    }

    public InaccessiblePathException(Throwable cause) {
        super("The path is not accessible", cause);
    }

    public InaccessiblePathException(String message) {
        super(message);
    }

    public InaccessiblePathException(String message, Throwable cause) {
        super(message, cause);
    }
}
