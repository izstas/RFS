package me.izstas.rfs.client.ui.model;

import java.util.Date;
import java.util.ResourceBundle;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.FormatUtil;
import me.izstas.rfs.model.FileMetadata;
import me.izstas.rfs.model.Metadata;

/**
 * A TreeTableModel representing a filesystem exposed by an RFS API.
 */
public final class RfsModel extends AbstractTreeTableModel {
    private static final ResourceBundle resources = ResourceBundle.getBundle("lang/BrowserForm");

    private final Rfs rfs;

    /**
     * Constructs the model.
     * @param rfs the RFS API
     */
    public RfsModel(Rfs rfs) {
        this.rfs = rfs;

        root = new RfsRootNode();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return resources.getString("tree.column.name");

            case 1:
                return resources.getString("tree.column.size");

            case 2:
                return resources.getString("tree.column.lastModified");

            case 3:
                return resources.getString("tree.column.attributes");
        }

        return null;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof RfsDummyNode && column == 0) {
            return resources.getString("tree.loading");
        }

        if (node instanceof RfsMetadataNode) {
            Metadata metadata = ((RfsMetadataNode) node).getMetadata();

            switch (column) {
                case 0:
                    return metadata.getName();

                case 1:
                    return metadata instanceof FileMetadata ? FormatUtil.formatSize(((FileMetadata) metadata).getSize()) : "";

                case 2:
                    return FormatUtil.formatDate(new Date(metadata.getLastModificationTime()));

                case 3:
                    return FormatUtil.formatAttributes(metadata.getAttributes());
            }
        }

        return null;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof RfsDummyNode ||
                (node instanceof RfsMetadataNode && ((RfsMetadataNode) node).getMetadata() instanceof FileMetadata);
    }

    @Override
    public int getChildCount(Object parent) {
        RfsMetadataNode parentNode = (RfsMetadataNode) parent;

        if (!parentNode.areChildrenRetrieved()) {
            parentNode.retrieveChildren(rfs, modelSupport);
        }

        return parentNode.getChildren().size();
    }

    @Override
    public Object getChild(Object parent, int index) {
        RfsMetadataNode parentNode = (RfsMetadataNode) parent;

        if (!parentNode.areChildrenRetrieved()) {
            parentNode.retrieveChildren(rfs, modelSupport);
        }

        return parentNode.getChildren().get(index);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        RfsMetadataNode parentNode = (RfsMetadataNode) parent;

        if (parentNode.areChildrenRetrieved()) {
            return parentNode.getChildren().indexOf(child);
        }
        else {
            return -1;
        }
    }
}
