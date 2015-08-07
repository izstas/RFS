package me.izstas.rfs.client.rfs;

/**
 * This exception is thrown when the API call results in an unparseable response.
 */
public class RfsResponseException extends RfsException {
    public RfsResponseException(Exception e) {
        super(e);
    }
}
