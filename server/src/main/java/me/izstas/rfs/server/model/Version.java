package me.izstas.rfs.server.model;

import java.util.List;

/**
 * @see me.izstas.rfs.server.controller.VersionController
 */
public class Version {
    private String id;
    private String version;
    private List<String> access;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getAccess() {
        return access;
    }

    public void setAccess(List<String> access) {
        this.access = access;
    }
}
