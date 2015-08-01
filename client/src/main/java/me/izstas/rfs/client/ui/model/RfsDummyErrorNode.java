package me.izstas.rfs.client.ui.model;

/**
 * A dummy node used as a child to represent that the real children could not be retrieved due to an error.
 */
public final class RfsDummyErrorNode extends RfsNode {
    private final Throwable cause;

    /**
     * Constructs the node.
     * @param parent the parent node
     * @param cause the exception representing the error
     */
    public RfsDummyErrorNode(RfsNode parent, Throwable cause) {
        super(parent);
        this.cause = cause;
    }

    /**
     * Returns the exception representing the error.
     */
    public Throwable getCause() {
        return cause;
    }
}
