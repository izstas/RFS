package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InaccessiblePathException extends RuntimeException {
    public InaccessiblePathException() {
        super("The path is not accessible");
    }

    public InaccessiblePathException(String message) {
        super(message);
    }

    public InaccessiblePathException(String message, Throwable cause) {
        super(message, cause);
    }
}
