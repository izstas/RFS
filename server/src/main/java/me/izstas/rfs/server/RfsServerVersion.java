package me.izstas.rfs.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This component contains the version information of this RFS server.
 * The information is retrieved from application configuration (rfs.version)
 */
@Component
public class RfsServerVersion {
    @Value("${rfs.version.id}")
    private String id;

    @Value("${rfs.version.short}")
    private String shortVersion;

    @Value("${rfs.version.full}")
    private String fullVersion;


    public String getId() {
        return id;
    }

    public String getShortVersion() {
        return shortVersion;
    }

    public String getFullVersion() {
        return fullVersion;
    }
}
