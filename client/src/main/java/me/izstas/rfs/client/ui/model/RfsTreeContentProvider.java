package me.izstas.rfs.client.ui.model;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.model.DirectoryMetadata;
import me.izstas.rfs.model.Metadata;

/**
 * A tree content provider for an RFS tree.
 */
public final class RfsTreeContentProvider implements ITreeContentProvider {
    private final Rfs rfs;
    private final TreeViewer viewer;

    /**
     * Constructs the tree content provider.
     * @param rfs the RFS API
     * @param viewer the viewer which will be notified about changes when necessary
     */
    public RfsTreeContentProvider(Rfs rfs, TreeViewer viewer) {
        this.rfs = rfs;
        this.viewer = viewer;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof RfsMetadataNode) {
            RfsMetadataNode node = (RfsMetadataNode) element;
            Metadata meta = node.getMetadata();
            return meta instanceof DirectoryMetadata && (!node.areChildrenRetrieved() || node.getChildren().length != 0);
        }

        return false;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof RfsMetadataNode) {
            RfsMetadataNode node = (RfsMetadataNode) parentElement;
            if (!node.areChildrenRetrieved()) {
                node.retrieveChildren(rfs, viewer);
            }

            return node.getChildren();
        }

        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return ((RfsNode) element).getParent();
    }
}
