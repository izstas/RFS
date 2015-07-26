package me.izstas.rfs.client.ui.model;

/**
 * A dummy node used as a temporary child to represent that the real children are currently being retrieved.
 */
final class RfsDummyNode extends RfsNode {
    public static final RfsNode INSTANCE = new RfsDummyNode();

    private RfsDummyNode() {
    }
}
