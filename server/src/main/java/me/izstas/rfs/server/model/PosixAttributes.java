package me.izstas.rfs.server.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("posix")
public class PosixAttributes extends Attributes {
    private String user;
    private String group;
    private String permissions;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
