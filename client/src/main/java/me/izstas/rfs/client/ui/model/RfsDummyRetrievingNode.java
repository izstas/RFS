package me.izstas.rfs.client.ui.model;

/**
 * A dummy node used as a temporary child to represent that the real children are currently being retrieved.
 */
public final class RfsDummyRetrievingNode extends RfsNode {
    /**
     * Constructs the node.
     * @param parent the parent node
     */
    public RfsDummyRetrievingNode(RfsNode parent) {
        super(parent);
    }
}
