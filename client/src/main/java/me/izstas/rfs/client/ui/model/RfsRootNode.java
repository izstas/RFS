package me.izstas.rfs.client.ui.model;

/**
 * A specialization of {@link RfsMetadataNode} representing the root node.
 */
public class RfsRootNode extends RfsMetadataNode {
    public RfsRootNode() {
        super(null, null);
    }

    @Override
    public String getPath() {
        return "";
    }
}
