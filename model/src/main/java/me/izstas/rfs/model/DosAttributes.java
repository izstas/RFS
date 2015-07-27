package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents DOS (Windows) file attributes.
 * When produced by an RFS server, the fields of this model must never be {@code null}.
 * When produced by an RFS client, the fields of this model may be {@code null}, and {@code null} indicates that
 * the client wishes to leave the value unchanged.
 */
@JsonTypeName("dos")
public class DosAttributes extends Attributes {
    // We don't use primitive types here because we want all properties to be nullable
    private Boolean readOnly;
    private Boolean hidden;
    private Boolean system;
    private Boolean archive;

    /**
     * Returns the {@code readOnly} component of this model, representing the "Read only" attribute.
     */
    public Boolean getReadOnly() {
        return readOnly;
    }

    /**
     * Sets the {@code readOnly} component of this model, representing the "Read only" attribute.
     */
    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Returns the {@code hidden} component of this model, representing the "Hidden" attribute.
     */
    public Boolean getHidden() {
        return hidden;
    }

    /**
     * Sets the {@code hidden} component of this model, representing the "Hidden" attribute.
     */
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Returns the {@code system} component of this model, representing the "System" attribute.
     */
    public Boolean getSystem() {
        return system;
    }

    /**
     * Sets the {@code system} component of this model, representing the "System" attribute.
     */
    public void setSystem(Boolean system) {
        this.system = system;
    }

    /**
     * Returns the {@code archive} component of this model, representing the "Archive" attribute.
     */
    public Boolean getArchive() {
        return archive;
    }

    /**
     * Sets the {@code archive} component of this model, representing the "Archive" attribute.
     */
    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
}
