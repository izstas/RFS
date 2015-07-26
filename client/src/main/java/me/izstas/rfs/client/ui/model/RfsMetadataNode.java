package me.izstas.rfs.client.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.TreePath;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.jdesktop.swingx.tree.TreeModelSupport;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.SwingExecutor;
import me.izstas.rfs.model.DirectoryMetadata;
import me.izstas.rfs.model.Metadata;

/**
 * A node that wraps a {@link Metadata} object, representing a file or a directory.
 */
public class RfsMetadataNode extends RfsNode {
    private final TreePath treePath;

    private Metadata metadata;
    private List<RfsNode> children;
    private boolean childrenRequested;
    private boolean childrenRetrieved;

    /**
     * Constructs the node as a root node of the tree.
     * @param metadata the metadata to be wrapped in this node
     */
    public RfsMetadataNode(Metadata metadata) {
        this.treePath = new TreePath(this);
        this.metadata = metadata;
    }

    /**
     * Constructs the node.
     * @param parentPath the path to the parent node in the tree
     * @param metadata the metadata to be wrapped in this node
     */
    public RfsMetadataNode(TreePath parentPath, Metadata metadata) {
        this.treePath = parentPath.pathByAddingChild(this);
        this.metadata = metadata;
    }

    /**
     * Returns RFS path to the file or directory represented by this node.
     */
    public String getRfsPath() {
        return ((RfsMetadataNode) treePath.getParentPath().getLastPathComponent()).getRfsPath() + '/' + metadata.getName();
    }

    /**
     * Returns the wrapped {@link Metadata} object.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Checks whether the children of this node have already been retrieved.
     * @return true if children have already been retrieved, false otherwise
     */
    public boolean areChildrenRetrieved() {
        return childrenRetrieved;
    }

    /**
     * Retrieves the children from the RFS API.
     * @param rfs the RFS API to request children from
     * @param support the {@link TreeModelSupport} which will be used to notify about model changes
     */
    public void retrieveChildren(final Rfs rfs, final TreeModelSupport support) {
        if (childrenRequested) {
            return;
        }

        childrenRequested = true;
        children = Collections.singletonList(RfsDummyNode.INSTANCE);

        Futures.addCallback(rfs.getMetadata(getRfsPath()), new FutureCallback<Metadata>() {
            @Override
            public void onSuccess(Metadata meta) {
                metadata = meta;
                childrenRetrieved = true;

                support.fireChildRemoved(treePath, 0, RfsDummyNode.INSTANCE);

                if (meta instanceof DirectoryMetadata) {
                    DirectoryMetadata dirMeta = (DirectoryMetadata) meta;

                    int[] indices = new int[dirMeta.getContents().size()];
                    children = new ArrayList<>();
                    for (int i = 0; i < dirMeta.getContents().size(); i++) {
                        indices[i] = i;
                        children.add(new RfsMetadataNode(treePath, dirMeta.getContents().get(i)));
                    }

                    support.fireChildrenAdded(treePath, indices, children.toArray(new RfsNode[children.size()]));
                }
                else {
                    children = Collections.emptyList();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                // TODO: Handle
            }
        }, SwingExecutor.INSTANCE);
    }

    /**
     * Returns the children of this node.
     */
    public List<RfsNode> getChildren() {
        return children;
    }
}
