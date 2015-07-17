package me.izstas.rfs.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.izstas.rfs.server.RfsException;

@ResponseStatus(HttpStatus.CONFLICT)
public class IncompatibleMetadataException extends RfsException {
    public IncompatibleMetadataException(String message) {
        super(message);
    }

    public IncompatibleMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
