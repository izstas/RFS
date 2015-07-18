package me.izstas.rfs.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(FileMetadata.class), @JsonSubTypes.Type(DirectoryMetadata.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {
    // We don't use primitive types here because we want all properties to be nullable
    private String name;
    private Long creationTime;
    private Long lastAccessTime;
    private Long lastModificationTime;
    private Attributes attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Long getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Long lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}
