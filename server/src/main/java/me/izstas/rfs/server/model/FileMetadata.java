package me.izstas.rfs.server.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("file")
public class FileMetadata extends Metadata {
    // We don't use primitive types here because we want all properties to be nullable
    private Long size;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
