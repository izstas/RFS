package me.izstas.rfs.server;

public class RfsException extends RuntimeException {
    public RfsException(String message) {
        super(message);
    }

    public RfsException(String message, Throwable cause) {
        super(message, cause);
    }
}
