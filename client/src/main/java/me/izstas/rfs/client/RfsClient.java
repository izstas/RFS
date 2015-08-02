package me.izstas.rfs.client;

import org.eclipse.swt.widgets.Display;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.MainWindow;

public class RfsClient {
    public static void main(String[] args) {
        new PreInitializationThread().start();

        Display display = new Display();

        MainWindow window = new MainWindow();
        window.setBlockOnOpen(true);
        window.open();

        display.dispose();
    }


    private static final class PreInitializationThread extends Thread {
        @Override
        public void run() {
            Rfs.preInitialize();
        }
    }
}
