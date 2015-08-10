package me.izstas.rfs.client.ui;

import java.lang.reflect.InvocationTargetException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.model.RfsMetadataNode;
import me.izstas.rfs.client.ui.model.RfsRootNode;
import me.izstas.rfs.client.ui.model.RfsTreeContentProvider;
import me.izstas.rfs.client.ui.model.RfsTreeColumnLabelProviders;
import me.izstas.rfs.client.util.SwtAsyncExecutor;
import me.izstas.rfs.model.FileMetadata;
import me.izstas.rfs.model.Metadata;

/**
 * The main window.
 */
public final class MainWindow extends ApplicationWindow {
    private TreeViewer rfsTreeViewer;
    private Action connectAction;
    private Action refreshAllAction;
    private Action exitAction;
    private Action downloadAction;
    private Action attributesAction;

    private Rfs rfs;

    public MainWindow() {
        super(null);
        createActions();
        addMenuBar();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);

        newShell.setText(Messages.MainWindow_title);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(1, false);
        containerLayout.marginWidth = 0;
        containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        rfsTreeViewer = new TreeViewer(container, SWT.NONE);
        addTreeViewerMenu();
        Tree rfsTree = rfsTreeViewer.getTree();
        rfsTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        rfsTree.setHeaderVisible(true);

        TreeViewerColumn rfsTreeViewerNameColumn = new TreeViewerColumn(rfsTreeViewer, SWT.NONE);
        rfsTreeViewerNameColumn.setLabelProvider(new RfsTreeColumnLabelProviders.Name());
        TreeColumn rfsTreeNameColumn = rfsTreeViewerNameColumn.getColumn();
        rfsTreeNameColumn.setWidth(300);
        rfsTreeNameColumn.setText(Messages.MainWindow_tree_column_name);

        TreeViewerColumn rfsTreeViewerSizeColumn = new TreeViewerColumn(rfsTreeViewer, SWT.NONE);
        rfsTreeViewerSizeColumn.setLabelProvider(new RfsTreeColumnLabelProviders.Size());
        TreeColumn rfsTreeSizeColumn = rfsTreeViewerSizeColumn.getColumn();
        rfsTreeSizeColumn.setWidth(100);
        rfsTreeSizeColumn.setText(Messages.MainWindow_tree_column_size);

        TreeViewerColumn rfsTreeViewerModifiedColumn = new TreeViewerColumn(rfsTreeViewer, SWT.NONE);
        rfsTreeViewerModifiedColumn.setLabelProvider(new RfsTreeColumnLabelProviders.DateModified());
        TreeColumn rfsTreeModifiedColumn = rfsTreeViewerModifiedColumn.getColumn();
        rfsTreeModifiedColumn.setWidth(200);
        rfsTreeModifiedColumn.setText(Messages.MainWindow_tree_column_dateModified);

        TreeViewerColumn rfsTreeViewerAttributesColumn = new TreeViewerColumn(rfsTreeViewer, SWT.NONE);
        rfsTreeViewerAttributesColumn.setLabelProvider(new RfsTreeColumnLabelProviders.Attributes());
        TreeColumn rfsTreeAttributesColumn = rfsTreeViewerAttributesColumn.getColumn();
        rfsTreeAttributesColumn.setWidth(200);
        rfsTreeAttributesColumn.setText(Messages.MainWindow_tree_column_attributes);

        return container;
    }

    private void createActions() {
        // Server menu
        connectAction = new Action(Messages.MainWindow_action_connect) {
            @Override
            public void run() {
                ServerDialog serverDialog = new ServerDialog(getShell());
                if (serverDialog.open() == OK) {
                    if (rfs != null) {
                        rfs.shutdown();
                    }

                    rfs = serverDialog.getRfs();
                    rfsTreeViewer.setContentProvider(new RfsTreeContentProvider(rfs, rfsTreeViewer));
                    rfsTreeViewer.setInput(new RfsRootNode());
                    refreshAllAction.setEnabled(true);
                }
            }
        };

        refreshAllAction = new Action(Messages.MainWindow_action_refreshAll) {
            @Override
            public void run() {
                if (rfsTreeViewer.getInput() != null) {
                    ((RfsRootNode) rfsTreeViewer.getInput()).retrieveChildren(rfs, rfsTreeViewer);
                    rfsTreeViewer.refresh();
                }
            }
        };
        refreshAllAction.setEnabled(false);
        refreshAllAction.setAccelerator(SWT.CTRL | SWT.F5);

        exitAction = new Action(Messages.MainWindow_action_exit) {
            @Override
            public void run() {
                getShell().close();
            }
        };
        exitAction.setAccelerator(SWT.ALT | SWT.F4);

        // Tree context menu
        downloadAction = new Action(Messages.MainWindow_action_download) {
            @Override
            public void run() {
                final RfsMetadataNode node = (RfsMetadataNode) ((ITreeSelection) rfsTreeViewer.getSelection()).getFirstElement();

                FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                fileDialog.setFileName(node.getMetadata().getName());
                fileDialog.setOverwrite(true);

                String filePath = fileDialog.open();
                if (filePath != null) {
                    ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(getShell());

                    try {
                        progressMonitorDialog.run(true, false,
                                new Downloader(rfs, node.getPath(), filePath, ((FileMetadata) node.getMetadata()).getSize()));
                    }
                    catch (InvocationTargetException e) {
                        MessageDialog.openError(getShell(), Messages.MainWindow_download_error_title,
                                String.format(Messages.MainWindow_download_error_message, Messages.getForException(e.getCause())));
                    }
                    catch (InterruptedException e) {
                        throw new AssertionError(e); // Task is not cancellable
                    }
                }
            }
        };
        downloadAction.setEnabled(false);
        downloadAction.setAccelerator(SWT.CR);

        attributesAction = new Action(Messages.MainWindow_action_attributes) {
            @Override
            public void run() {
                final RfsMetadataNode node = (RfsMetadataNode) ((ITreeSelection) rfsTreeViewer.getSelection()).getFirstElement();

                AttributesDialog attributesDialog = new AttributesDialog(getShell(), rfs, node.getPath(), node.getMetadata());
                if (attributesDialog.open() == OK) {
                    Futures.addCallback(attributesDialog.getFuture(), new FutureCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            ((RfsMetadataNode) node.getParent()).retrieveChildren(rfs, rfsTreeViewer);
                            rfsTreeViewer.refresh(node.getParent());
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            // AttributesDialog will show the error message
                        }
                    }, SwtAsyncExecutor.INSTANCE);
                }
            }
        };
        attributesAction.setEnabled(false);
        attributesAction.setAccelerator(SWT.ALT | SWT.CR);
    }

    @Override
    protected MenuManager createMenuManager() {
        MenuManager menu = new MenuManager();

        MenuManager serverMenu = new MenuManager(Messages.MainWindow_menu_server);
        menu.add(serverMenu);

        serverMenu.add(connectAction);
        serverMenu.add(refreshAllAction);
        serverMenu.add(new Separator());
        serverMenu.add(exitAction);

        return menu;
    }

    private void addTreeViewerMenu() {
        MenuManager menu = new MenuManager();
        menu.add(downloadAction);
        menu.add(attributesAction);

        rfsTreeViewer.getTree().setMenu(menu.createContextMenu(rfsTreeViewer.getTree()));
        rfsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object node = ((ITreeSelection) event.getSelection()).getFirstElement();
                if (node != null && node instanceof RfsMetadataNode) {
                    Metadata meta = ((RfsMetadataNode) node).getMetadata();

                    downloadAction.setEnabled(meta instanceof FileMetadata);
                    attributesAction.setEnabled(true);
                }
                else {
                    downloadAction.setEnabled(false);
                    attributesAction.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected Point getInitialSize() {
        return new Point(833, 500);
    }

    @Override
    public boolean close() {
        if (rfs != null) {
            rfs.shutdown();
        }

        return super.close();
    }
}
