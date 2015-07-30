package me.izstas.rfs.client.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public final class ServerDialog extends Dialog {
    private static final int CHECK_ID = IDialogConstants.CLIENT_ID + 1;

    private Text urlText;
    private Text authUserText;
    private Text authPwdText;

    public ServerDialog(Shell parentShell) {
        super(parentShell);
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
        urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group authGroup = new Group(container, SWT.NONE);
        authGroup.setLayout(new GridLayout(2, false));
        GridData gd_authGroup = new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1);
        gd_authGroup.widthHint = 417;
        authGroup.setLayoutData(gd_authGroup);
        authGroup.setText(Messages.ServerDialog_authentication);

        Button authAnonCheck = new Button(authGroup, SWT.CHECK);
        authAnonCheck.setSelection(true);
        authAnonCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        authAnonCheck.setText(Messages.ServerDialog_authentication_anonymous);

        Label authUserLabel = new Label(authGroup, SWT.NONE);
        authUserLabel.setText(Messages.ServerDialog_authentication_username);

        authUserText = new Text(authGroup, SWT.BORDER);
        authUserText.setEnabled(false);
        authUserText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label authPwdLabel = new Label(authGroup, SWT.NONE);
        authPwdLabel.setText(Messages.ServerDialog_authentication_password);

        authPwdText = new Text(authGroup, SWT.BORDER);
        authPwdText.setEnabled(false);
        authPwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label statusLabel = new Label(container, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CHECK_ID, Messages.ServerDialog_check, false);
        createButton(parent, IDialogConstants.OK_ID, Messages.ServerDialog_browse, true);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(451, 254);
    }
}
