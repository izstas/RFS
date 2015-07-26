package me.izstas.rfs.client;

import javax.swing.JFrame;
import javax.swing.UIManager;

import me.izstas.rfs.client.ui.ServerForm;

public class RfsClient {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // Should not really hurt, but we probably want to handle it properly later
        }

        JFrame frame = ServerForm.createFrame();
        frame.setVisible(true);
    }
}
