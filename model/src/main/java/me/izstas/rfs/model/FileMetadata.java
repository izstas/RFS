package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents file metadata.
 * When produced by an RFS server, the fields of this model must never be {@code null}.
 * When produced by an RFS client, the fields of this model may be {@code null}, and {@code null} indicates that
 * the client wishes to leave the value unchanged.
 */
@JsonTypeName("file")
public class FileMetadata extends Metadata {
    // We don't use primitive types here because we want all properties to be nullable
    private Long size;

    /**
     * Returns the {@code size} component of this model, representing the file size in bytes.
     */
    public Long getSize() {
        return size;
    }

    /**
     * Sets the {@code size} component of this model, representing the file size in bytes.
     */
    public void setSize(Long size) {
        this.size = size;
    }
}
