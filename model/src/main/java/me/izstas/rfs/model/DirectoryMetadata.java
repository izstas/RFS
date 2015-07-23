package me.izstas.rfs.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents directory metadata.
 */
@JsonTypeName("directory")
public class DirectoryMetadata extends Metadata {
    private List<Metadata> contents;

    public List<Metadata> getContents() {
        return contents;
    }

    public void setContents(List<Metadata> contents) {
        this.contents = contents;
    }
}