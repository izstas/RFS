package me.izstas.rfs.model;

import java.util.List;

/**
 * Represents basic RFS server information, including version and user's access level.
 * The fields of this model must never be {@code null}.
 */
public class Version {
    private String id;
    private String version;
    private List<String> access;

    /**
     * Returns the {@code id} component of this model, representing the type of the server.
     * Clients may use it to determine whether the API they're calling is actually an RFS server.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the {@code id} component of this model, representing the type of the server.
     * @see #getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the {@code version} component of this model, representing the version string of the RFS server.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the {@code version} component of this model, representing the version string of the RFS server.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the {@code access} component of this model, representing the access level of the API caller.
     * @return a list which may contain the following values:
     *         <ul>
     *           <li>{@code "read"} - if the caller has read access to the RFS server</li>
     *           <li>{@code "write"} - if the caller has write access to the RFS server</li>
     *         </ul>
     */
    public List<String> getAccess() {
        return access;
    }

    /**
     * Sets the {@code access} component of this model, representing the access level of the API caller.
     * @see #getAccess()
     */
    public void setAccess(List<String> access) {
        this.access = access;
    }
}
