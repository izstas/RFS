package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents POSIX (*nix) file attributes.
 * When produced by an RFS server, the fields of this model must never be {@code null}.
 * When produced by an RFS client, the fields of this model may be {@code null}, and {@code null} indicates that
 * the client wishes to leave the value unchanged.
 */
@JsonTypeName("posix")
public class PosixAttributes extends Attributes {
    private String user;
    private String group;
    private String permissions;

    /**
     * Returns the {@code user} component of this model, representing the name of the user-owner of the file.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the {@code user} component of this model, representing the name of the user-owner of the file.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Returns the {@code group} component of this model, representing the name of the group-owner of the file.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the {@code group} component of this model, representing the name of the group-owner of the file.
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Returns the {@code permissions} component of this model, representing the POSIX file permissions of the file.
     * @see java.nio.file.attribute.PosixFilePermissions#toString(java.util.Set)
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * Sets the {@code permissions} component of this model, representing the POSIX file permissions of the file.
     * @see java.nio.file.attribute.PosixFilePermissions#toString(java.util.Set)
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
