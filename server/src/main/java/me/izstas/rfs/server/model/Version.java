package me.izstas.rfs.server.model;

/**
 * @see me.izstas.rfs.server.controller.VersionController
 */
public class Version {
    private final String id;
    private final String version;

    public Version(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }
}
