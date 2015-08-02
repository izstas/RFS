package me.izstas.rfs.client.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.model.RfsRootNode;
import me.izstas.rfs.client.ui.model.RfsTreeContentProvider;
import me.izstas.rfs.client.ui.model.RfsTreeColumnLabelProviders;

/**
 * The main window.
 */
public final class MainWindow extends ApplicationWindow {
    private TreeViewer rfsTreeViewer;
    private Action connectAction;
    private Action exitAction;

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
        newShell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                if (rfs != null) {
                    rfs.shutdown();
                }
            }
        });
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(1, false);
        containerLayout.marginWidth = 0;
        containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        rfsTreeViewer = new TreeViewer(container, SWT.NONE);
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
        rfsTreeSizeColumn.setWidth(75);
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
                }
            }
        };

        exitAction = new Action(Messages.MainWindow_action_exit) {
            @Override
            public void run() {
                getShell().close();
            }
        };
        exitAction.setAccelerator(SWT.ALT | SWT.F4);
    }

    @Override
    protected MenuManager createMenuManager() {
        MenuManager menu = new MenuManager();

        MenuManager serverMenu = new MenuManager(Messages.MainWindow_menu_server);
        menu.add(serverMenu);

        serverMenu.add(connectAction);
        serverMenu.add(new Separator());
        serverMenu.add(exitAction);

        return menu;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(808, 500);
    }
}
