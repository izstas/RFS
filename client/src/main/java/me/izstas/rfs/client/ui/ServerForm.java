package me.izstas.rfs.client.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.rfs.RfsAuthenticationException;
import me.izstas.rfs.client.rfs.RfsResponseException;
import me.izstas.rfs.client.util.SwingExecutor;
import me.izstas.rfs.model.Version;

public final class ServerForm {
    private static final Color STATUS_SUCCESS_COLOR = new Color(0, 100, 0);
    private static final Color STATUS_FAILURE_COLOR = new Color(100, 0, 0);
    private static final ResourceBundle resources = ResourceBundle.getBundle("lang/ServerForm");

    private JPanel rootPanel;
    private JTextField urlField;
    private JCheckBox authAnonymousCheckBox;
    private JTextField authUsernameField;
    private JPasswordField authPasswordField;
    private JLabel statusLabel;
    private JButton checkButton;
    private JButton browseButton;

    private Rfs checkRfs;
    private ListenableFuture<Version> checkFuture;

    private ServerForm() {
        authAnonymousCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authUsernameField.setEnabled(!authAnonymousCheckBox.isSelected());
                authPasswordField.setEnabled(!authAnonymousCheckBox.isSelected());
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                check();
            }
        });
    }

    public static JFrame createFrame() {
        final ServerForm form = new ServerForm();

        JFrame frame = new JFrame(resources.getString("title"));
        frame.setContentPane(form.rootPanel);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                form.cancelActiveCheck();
            }
        });

        return frame;
    }


    private void check() {
        statusLabel.setForeground(UIManager.getColor("Label.foreground"));
        statusLabel.setText(resources.getString("status.checking"));

        cancelActiveCheck();

        try {
            checkRfs = createRfs();
        }
        catch (URISyntaxException e) {
            checkCompleted(false, "status.failure.url");
            return;
        }

        checkFuture = checkRfs.getVersion();
        Futures.addCallback(checkFuture, new FutureCallback<Version>() {
            @Override
            public void onSuccess(Version v) {
                if (!"me.izstas.rfs".equals(v.getId())) {
                    checkCompleted(false, "status.failure.response");
                    return;
                }

                if (!v.getAccess().contains("read")) {
                    checkCompleted(false, "status.failure.noRead");
                    return;
                }

                if (v.getAccess().contains("write")) {
                    checkCompleted(true, "status.success.readWrite");
                }
                else {
                    checkCompleted(true, "status.success.readOnly");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (e instanceof CancellationException) {
                    // Ignore. We should be starting a new check now
                }
                else if (e instanceof RfsAuthenticationException) {
                    checkCompleted(false, "status.failure.authentication");
                }
                else if (e instanceof RfsResponseException) {
                    checkCompleted(false, "status.failure.response");
                }
                else {
                    checkCompleted(false, "status.failure.generic", e.getClass(), e.getMessage());
                }
            }
        }, SwingExecutor.INSTANCE);
    }

    private void checkCompleted(boolean success, String status, Object... statusArgs) {
        statusLabel.setForeground(success ? STATUS_SUCCESS_COLOR : STATUS_FAILURE_COLOR);
        statusLabel.setText(String.format(resources.getString(status), statusArgs));
    }

    private void cancelActiveCheck() {
        if (checkFuture != null && !checkFuture.isDone()) {
            checkFuture.cancel(false);
        }

        if (checkRfs != null) {
            checkRfs.shutdown();
        }
    }


    private Rfs createRfs() throws URISyntaxException {
        URI uri = new URI(urlField.getText());

        if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new URISyntaxException(urlField.getText(), "scheme must be http or https");
        }

        if (authAnonymousCheckBox.isSelected()) {
            return new Rfs(uri);
        }
        else {
            return new Rfs(uri, authUsernameField.getText(), new String(authPasswordField.getPassword()));
        }
    }
}
