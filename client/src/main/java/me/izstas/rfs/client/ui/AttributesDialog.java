package me.izstas.rfs.client.ui;

import java.util.Date;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.SwtAsyncExecutor;
import me.izstas.rfs.model.Metadata;

/**
 * A dialog for modifying file attributes.
 */
public final class AttributesDialog extends Dialog {
    private final Rfs rfs;
    private final String path;
    private final Metadata metadata;

    private Text nameText;
    private DateTimeComposite dateCreatedDateTime;
    private DateTimeComposite dateAccessedDateTime;
    private DateTimeComposite dateModifiedDateTime;

    private ListenableFuture<Void> future;

    /**
     * Constructs the dialog.
     * @param rfs the RFS API
     * @param path the RFS path of the file we're modifying metadata of
     * @param metadata current file metadata
     */
    public AttributesDialog(Shell parentShell, Rfs rfs, String path, Metadata metadata) {
        super(parentShell);
        this.rfs = rfs;
        this.path = path;
        this.metadata = metadata;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText(Messages.AttributesDialog_title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText(Messages.AttributesDialog_name);

        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        nameText.setText(metadata.getName());

        Label dateCreatedLabel = new Label(container, SWT.NONE);
        dateCreatedLabel.setText(Messages.AttributesDialog_dateCreated);

        dateCreatedDateTime = new DateTimeComposite(container, SWT.NONE);
        dateCreatedDateTime.setDate(new Date(metadata.getCreationTime()));

        Label dateAccessedLabel = new Label(container, SWT.NONE);
        dateAccessedLabel.setText(Messages.AttributesDialog_dateAccessed);

        dateAccessedDateTime = new DateTimeComposite(container, SWT.NONE);
        dateAccessedDateTime.setDate(new Date(metadata.getLastAccessTime()));

        Label dateModifiedLabel = new Label(container, SWT.NONE);
        dateModifiedLabel.setText(Messages.AttributesDialog_dateModified);

        dateModifiedDateTime = new DateTimeComposite(container, SWT.NONE);
        dateModifiedDateTime.setDate(new Date(metadata.getLastModificationTime()));

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        Metadata metaToApply = new Metadata();

        metaToApply.setName(nameText.getText());
        metaToApply.setCreationTime(dateCreatedDateTime.getDate().getTime());
        metaToApply.setLastAccessTime(dateAccessedDateTime.getDate().getTime());
        metaToApply.setLastModificationTime(dateModifiedDateTime.getDate().getTime());

        future = rfs.applyMetadata(path, metaToApply);
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            }

            @Override
            public void onFailure(Throwable e) {
                MessageBox messageBox = new MessageBox(getParentShell(), SWT.ICON_ERROR);
                messageBox.setText(Messages.AttributesDialog_error_title);
                messageBox.setMessage(String.format(Messages.AttributesDialog_error_message, Messages.getForException(e)));
                messageBox.open();
            }
        }, SwtAsyncExecutor.INSTANCE);

        setReturnCode(OK);
        close();
    }

    /**
     * Returns the future corresponding to the operation of applying metadata.
     * @return the future, or {@code null} if the operation was not started
     */
    public ListenableFuture<Void> getFuture() {
        return future;
    }
}
