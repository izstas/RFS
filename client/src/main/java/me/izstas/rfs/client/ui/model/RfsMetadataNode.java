package me.izstas.rfs.client.ui.model;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.eclipse.jface.viewers.TreeViewer;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.util.SwtAsyncExecutor;
import me.izstas.rfs.model.DirectoryMetadata;
import me.izstas.rfs.model.Metadata;

/**
 * A node that wraps a {@link Metadata} object, representing a file or a directory.
 */
public class RfsMetadataNode extends RfsNode {
    private Metadata metadata;
    private RfsNode[] children;
    private boolean childrenRequested;
    private boolean childrenRetrieved;

    /**
     * Constructs the node.
     * @param parent the parent node
     * @param metadata the metadata to be wrapped in this node
     */
    public RfsMetadataNode(RfsNode parent, Metadata metadata) {
        super(parent);
        this.metadata = metadata;
    }

    /**
     * Returns RFS path to the file or directory represented by this node.
     */
    public String getRfsPath() {
        return ((RfsMetadataNode) parent).getRfsPath() + '/' + metadata.getName();
    }

    /**
     * Returns the wrapped {@link Metadata} object.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Checks whether the children of this node have already been retrieved.
     * @return {@code true} if children have already been retrieved, {@code false} otherwise
     */
    public boolean areChildrenRetrieved() {
        return childrenRetrieved;
    }

    /**
     * Retrieves the children from the RFS API.
     * @param rfs the RFS API to request children from
     * @param viewer the {@link TreeViewer} which will be refreshed when the operation completes
     */
    public void retrieveChildren(final Rfs rfs, final TreeViewer viewer) {
        if (childrenRequested) {
            return;
        }

        childrenRequested = true;
        children = new RfsNode[] {new RfsDummyRetrievingNode(this)};

        Futures.addCallback(rfs.getMetadata(getRfsPath()), new FutureCallback<Metadata>() {
            @Override
            public void onSuccess(Metadata meta) {
                metadata = meta;
                childrenRequested = false;
                childrenRetrieved = true;

                if (meta instanceof DirectoryMetadata) {
                    DirectoryMetadata dirMeta = (DirectoryMetadata) meta;

                    children = new RfsNode[dirMeta.getContents().size()];
                    for (int i = 0; i < children.length; i++) {
                        children[i] = new RfsMetadataNode(RfsMetadataNode.this, dirMeta.getContents().get(i));
                    }
                }
                else {
                    children = new RfsNode[0];
                }

                viewer.refresh(RfsMetadataNode.this);
            }

            @Override
            public void onFailure(Throwable e) {
                childrenRequested = false;
                childrenRetrieved = true;
                children = new RfsNode[] {new RfsDummyErrorNode(RfsMetadataNode.this, e)};

                viewer.refresh(RfsMetadataNode.this);
            }
        }, SwtAsyncExecutor.INSTANCE);
    }

    /**
     * Returns the children of this node.
     */
    public RfsNode[] getChildren() {
        return children;
    }
}
