package me.izstas.rfs.client.ui.model;

/**
 * A base class for all RFS tree nodes.
 */
public abstract class RfsNode {
    protected final RfsNode parent;

    /**
     * Constructs the node.
     * @param parent the parent node, or {@code null} if this is a root node
     */
    public RfsNode(RfsNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent of this node.
     * @return the parent node, or {@code null} if this is a root node
     */
    public final RfsNode getParent() {
        return parent;
    }
}
