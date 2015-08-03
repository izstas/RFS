package me.izstas.rfs.client.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream which notifies when it's getting written into.
 * @see MonitoredOutputStreamListener
 */
public final class MonitoredOutputStream extends FilterOutputStream {
    private final MonitoredOutputStreamListener listener;

    public MonitoredOutputStream(OutputStream out, MonitoredOutputStreamListener listener) {
        super(out);
        this.listener = listener;
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        listener.bytesWritten(1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        listener.bytesWritten(len);
    }
}
