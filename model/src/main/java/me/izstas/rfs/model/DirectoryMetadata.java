package me.izstas.rfs.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents directory metadata.
 * When produced by an RFS server, the fields of this model must never be {@code null}.
 * When produced by an RFS client, the fields of this model may be {@code null}, and {@code null} indicates that
 * the client wishes to leave the value unchanged.
 */
@JsonTypeName("directory")
public class DirectoryMetadata extends Metadata {
    private List<Metadata> contents;

    /**
     * Returns the {@code contents} component of this model, representing the children of the directory.
     */
    public List<Metadata> getContents() {
        return contents;
    }

    /**
     * Sets the {@code contents} component of this model, representing the children of the directory.
     */
    public void setContents(List<Metadata> contents) {
        this.contents = contents;
    }
}
