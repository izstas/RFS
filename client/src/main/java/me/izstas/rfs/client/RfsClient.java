package me.izstas.rfs.client;

import javax.swing.JFrame;
import javax.swing.UIManager;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.ServerForm;

public class RfsClient {
    public static void main(String[] args) {
        new PreInitializationThread().start();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // Should not really hurt, but we probably want to handle it properly later
        }

        JFrame frame = ServerForm.createFrame();
        frame.setVisible(true);
    }


    private static final class PreInitializationThread extends Thread {
        @Override
        public void run() {
            Rfs.preInitialize();
        }
    }
}
