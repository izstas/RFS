package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.izstas.rfs.server.RfsException;

/**
 * This exception is thrown when the client requests to apply invalid metadata to a file or a directory.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadMetadataException extends RfsException {
    public BadMetadataException(String message) {
        super(message);
    }

    public BadMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
