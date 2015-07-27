package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents file or directory metadata.
 * When produced by an RFS server, the fields of this model must never be {@code null}.
 * When produced by an RFS client, the fields of this model may be {@code null}, and {@code null} indicates that
 * the client wishes to leave the value unchanged.
 *
 * @see FileMetadata
 * @see DirectoryMetadata
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Metadata.class)
@JsonSubTypes({@JsonSubTypes.Type(FileMetadata.class), @JsonSubTypes.Type(DirectoryMetadata.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {
    // We don't use primitive types here because we want all properties to be nullable
    private String name;
    private Long creationTime;
    private Long lastAccessTime;
    private Long lastModificationTime;
    private Attributes attributes;

    /**
     * Returns the {@code name} component of this model, representing the file name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the {@code name} component of this model, representing the file name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the {@code creationTime} component of this model, representing the creation time of file in the form of a UNIX timestamp.
     */
    public Long getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the {@code creationTime} component of this model, representing the creation time of file in the form of a UNIX timestamp.
     */
    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Returns the {@code lastAccessTime} component of this model, representing the last access time of file in the form of a UNIX timestamp.
     */
    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Sets the {@code lastAccessTime} component of this model, representing the last access time of file in the form of a UNIX timestamp.
     */
    public void setLastAccessTime(Long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Returns the {@code lastModificationTime} component of this model, representing the last modification time of file in the form of a UNIX timestamp.
     */
    public Long getLastModificationTime() {
        return lastModificationTime;
    }

    /**
     * Sets the {@code lastModificationTime} component of this model, representing the last modification time of file in the form of a UNIX timestamp.
     */
    public void setLastModificationTime(Long lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    /**
     * Returns the {@code attributes} component of this model, representing platform-specific file attributes.
     * @see Attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the {@code attributes} component of this model, representing platform-specific file attributes.
     * @see Attributes
     */
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}
