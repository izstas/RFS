package me.izstas.rfs.client.util;

import java.util.concurrent.Executor;
import javax.swing.SwingUtilities;

/**
 * An executor whose {@link Executor#execute(Runnable)} method delegates to {@link SwingUtilities#invokeLater(Runnable)}.
 */
public final class SwingExecutor implements Executor {
    public static final Executor INSTANCE = new SwingExecutor();

    private SwingExecutor() {
    }

    @Override
    public void execute(Runnable command) {
        SwingUtilities.invokeLater(command);
    }
}
