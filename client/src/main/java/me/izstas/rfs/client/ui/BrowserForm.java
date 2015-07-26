package me.izstas.rfs.client.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jdesktop.swingx.JXTreeTable;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.model.RfsModel;

public final class BrowserForm {
    private static final ResourceBundle resources = ResourceBundle.getBundle("lang/BrowserForm");

    private JPanel rootPanel;
    private JXTreeTable rfsTree;

    private Rfs rfs;

    public BrowserForm(Rfs rfs) {
        this.rfs = rfs;

        rfsTree.setTreeTableModel(new RfsModel(rfs));
    }

    public static JFrame createFrame(Rfs rfs) {
        final BrowserForm form = new BrowserForm(rfs);

        JFrame frame = new JFrame(resources.getString("title"));
        frame.setContentPane(form.rootPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                form.rfs.shutdown();
            }
        });

        return frame;
    }
}
