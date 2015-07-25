package me.izstas.rfs.client;

/**
 * A base class for exceptions indicating specific RFS API calling errors.
 */
public class RfsException extends RuntimeException {
    public RfsException() {
    }

    public RfsException(Throwable cause) {
        super(cause);
    }
}
