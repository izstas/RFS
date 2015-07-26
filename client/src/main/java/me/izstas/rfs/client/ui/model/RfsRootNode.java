package me.izstas.rfs.client.ui.model;

import me.izstas.rfs.model.Metadata;

/**
 * A specialization of {@link RfsMetadataNode} representing the root node of the tree.
 */
public class RfsRootNode extends RfsMetadataNode {
    public RfsRootNode() {
        super(new Metadata());
    }

    @Override
    public String getRfsPath() {
        return "";
    }
}
