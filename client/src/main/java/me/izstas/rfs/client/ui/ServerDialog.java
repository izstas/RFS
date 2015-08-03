package me.izstas.rfs.client.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CancellationException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.SwtAsyncExecutor;
import me.izstas.rfs.model.Version;

/**
 * A dialog prompting the user to enter server URL and credentials.
 */
public final class ServerDialog extends Dialog {
    private static final int CHECK_ID = IDialogConstants.CLIENT_ID + 1;
    private static final Color STATUS_SUCCESS_COLOR = new Color(Display.getCurrent(), 0, 100, 0);
    private static final Color STATUS_FAILURE_COLOR = new Color(Display.getCurrent(), 100, 0, 0);

    private Text urlText;
    private Button authAnonCheck;
    private Text authUserText;
    private Text authPwdText;
    private Label statusLabel;

    private Rfs checkRfs;
    private ListenableFuture<Version> checkFuture;

    private Rfs rfs;

    public ServerDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Returns the RFS object created based on the information specified by the user in this dialog.
     * @return the RFS object, or {@code null} if the object is not created (e.g. user didn't press OK)
     */
    public Rfs getRfs() {
        return rfs;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText(Messages.ServerDialog_title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        Label urlLabel = new Label(container, SWT.NONE);
        urlLabel.setText(Messages.ServerDialog_url);

        urlText = new Text(container, SWT.BORDER);
        GridData urlTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        urlTextGridData.widthHint = 400;
        urlText.setLayoutData(urlTextGridData);

        Group authGroup = new Group(container, SWT.NONE);
        authGroup.setLayout(new GridLayout(2, false));
        authGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
        authGroup.setText(Messages.ServerDialog_authentication);

        authAnonCheck = new Button(authGroup, SWT.CHECK);
        authAnonCheck.setSelection(true);
        authAnonCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        authAnonCheck.setText(Messages.ServerDialog_authentication_anonymous);
        authAnonCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                authUserText.setEnabled(!authAnonCheck.getSelection());
                authPwdText.setEnabled(!authAnonCheck.getSelection());
            }
        });

        Label authUserLabel = new Label(authGroup, SWT.NONE);
        authUserLabel.setText(Messages.ServerDialog_authentication_username);

        authUserText = new Text(authGroup, SWT.BORDER);
        authUserText.setEnabled(false);
        authUserText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label authPwdLabel = new Label(authGroup, SWT.NONE);
        authPwdLabel.setText(Messages.ServerDialog_authentication_password);

        authPwdText = new Text(authGroup, SWT.BORDER | SWT.PASSWORD);
        authPwdText.setEnabled(false);
        authPwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        statusLabel = new Label(container, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CHECK_ID, Messages.ServerDialog_check, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    public boolean close() {
        cancelActiveCheck();
        return super.close();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == CHECK_ID) {
            check();
        }
        else if (buttonId == IDialogConstants.OK_ID) {
            try {
                rfs = createRfs();
            }
            catch (URISyntaxException e) {
                check(); // Forcing a check will show user the error
                return;
            }

            setReturnCode(OK);
            close();
        }
    }


    private void check() {
        statusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
        statusLabel.setText(Messages.ServerDialog_status_checking);

        cancelActiveCheck();

        try {
            checkRfs = createRfs();
        }
        catch (URISyntaxException e) {
            checkCompleted(false, Messages.ServerDialog_status_failure_url);
            return;
        }

        checkFuture = checkRfs.getVersion();
        Futures.addCallback(checkFuture, new FutureCallback<Version>() {
            @Override
            public void onSuccess(Version version) {
                if (!"me.izstas.rfs".equals(version.getId())) {
                    checkCompleted(false, String.format(Messages.ServerDialog_status_failure, Messages.rfs_error_response));
                    return;
                }

                if (version.getAccess() == null || !version.getAccess().contains("read")) {
                    checkCompleted(false, Messages.ServerDialog_status_failure_noRead);
                    return;
                }

                if (version.getAccess().contains("write")) {
                    checkCompleted(true, Messages.ServerDialog_status_success_readWrite);
                }
                else {
                    checkCompleted(true, Messages.ServerDialog_status_success_readOnly);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (e instanceof CancellationException) {
                    // Ignore. We should be starting a new check now
                }
                else {
                    checkCompleted(false, String.format(Messages.ServerDialog_status_failure, Messages.getForException(e)));
                }
            }
        }, SwtAsyncExecutor.INSTANCE);
    }

    private void checkCompleted(boolean success, String status) {
        statusLabel.setForeground(success ? STATUS_SUCCESS_COLOR : STATUS_FAILURE_COLOR);
        statusLabel.setText(status);
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
        URI uri = new URI(urlText.getText());

        if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new URISyntaxException(urlText.getText(), "scheme must be http or https");
        }

        if (authAnonCheck.getSelection()) {
            return new Rfs(uri);
        }
        else {
            return new Rfs(uri, authUserText.getText(), authPwdText.getText());
        }
    }
}
