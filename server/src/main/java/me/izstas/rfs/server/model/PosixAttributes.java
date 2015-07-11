package me.izstas.rfs.server.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("posix")
public class PosixAttributes extends Attributes {
    // We don't use primitive types here because we want all properties to be nullable
    private String user;
    private String group;
    private Integer permissions;

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

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
