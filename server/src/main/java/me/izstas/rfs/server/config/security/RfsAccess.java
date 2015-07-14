package me.izstas.rfs.server.config.security;

public final class RfsAccess {
    public static final String READ = "read";
    public static final String WRITE = "write";

    private RfsAccess() {
        throw new AssertionError();
    }
}
