package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents DOS (Windows) file attributes.
 */
@JsonTypeName("dos")
public class DosAttributes extends Attributes {
    // We don't use primitive types here because we want all properties to be nullable
    private Boolean readOnly;
    private Boolean hidden;
    private Boolean system;
    private Boolean archive;

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
}
