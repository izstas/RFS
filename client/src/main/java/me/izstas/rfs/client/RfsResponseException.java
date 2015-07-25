package me.izstas.rfs.client;

/**
 * This exception is thrown when API calls results in an unparseable response.
 */
public class RfsResponseException extends RfsException {
    public RfsResponseException(Exception e) {
        super(e);
    }
}
