package me.izstas.rfs.client.util;

import java.util.concurrent.Executor;
import org.eclipse.swt.widgets.Display;

/**
 * An executor whose {@link Executor#execute(Runnable)} method delegates to the {@link Display#asyncExec(Runnable)} method of the default display.
 */
public final class SwtAsyncExecutor implements Executor {
    public static final Executor INSTANCE = new SwtAsyncExecutor();

    private SwtAsyncExecutor() {
    }

    @Override
    public void execute(Runnable command) {
        Display.getDefault().asyncExec(command);
    }
}
