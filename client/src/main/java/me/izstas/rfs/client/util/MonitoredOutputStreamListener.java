package me.izstas.rfs.client.util;

/**
 * A listener for {@link MonitoredOutputStream}.
 */
public interface MonitoredOutputStreamListener {
    /**
     * Gets called when some number of bytes has been written into the OutputStream.
     * @param count the number of bytes written
     */
    void bytesWritten(int count);
}
