package me.izstas.rfs.client.ui;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.MonitoredOutputStream;
import me.izstas.rfs.client.util.MonitoredOutputStreamListener;

/**
 * A {@link IRunnableWithProgress} which downloads a file from RFS.
 */
public final class Downloader implements IRunnableWithProgress {
    private final Rfs rfs;
    private final String rfsPath;
    private final String targetPath;
    private final long size;

    /**
     * Constructs the downloader.
     * @param rfs the RFS API
     * @param rfsPath the RFS path of the file to download
     * @param targetPath the path the file will be downloaded to
     * @param size the size of the file (used for indicating progress)
     */
    public Downloader(Rfs rfs, String rfsPath, String targetPath, long size) {
        this.rfs = rfs;
        this.rfsPath = rfsPath;
        this.targetPath = targetPath;
        this.size = size;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        int bytesToWorkScale = 1;
        if (size > Integer.MAX_VALUE) {
            bytesToWorkScale = 1024; // Should suffice for 2 TB files
        }

        monitor.beginTask(String.format(Messages.MainWindow_download_downloading, rfsPath), (int) (size / bytesToWorkScale));

        try (OutputStream out = new MonitoredOutputStream(new BufferedOutputStream(new FileOutputStream(targetPath)),
                new ProgressMonitorMonitoredOutputStreamListener(monitor, bytesToWorkScale))) {
            rfs.getContent(rfsPath, out);
        }
        catch (IOException e) {
            throw new InvocationTargetException(e);
        }

        monitor.done();
    }


    private static final class ProgressMonitorMonitoredOutputStreamListener implements MonitoredOutputStreamListener {
        private final IProgressMonitor monitor;
        private final int bytesToWorkScale;
        private int bytesCollected;

        ProgressMonitorMonitoredOutputStreamListener(IProgressMonitor monitor, int bytesToWorkScale) {
            this.monitor = monitor;
            this.bytesToWorkScale = bytesToWorkScale;
        }

        @Override
        public void bytesWritten(int count) {
            bytesCollected += count;

            int work = 0;
            while (bytesCollected >= bytesToWorkScale) {
                bytesCollected -= bytesToWorkScale;
                work++;
            }

            monitor.worked(work);
        }
    }
}
