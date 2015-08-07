package me.izstas.rfs.client.rfs;

/**
 * A base class for exceptions indicating RFS API calling errors.
 */
public class RfsException extends RuntimeException {
    public RfsException() {
    }

    public RfsException(Throwable cause) {
        super(cause);
    }
}
