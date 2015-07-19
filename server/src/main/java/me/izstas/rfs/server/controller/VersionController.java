package me.izstas.rfs.server.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.izstas.rfs.server.RfsServerVersion;
import me.izstas.rfs.server.model.Version;

/**
 * The main purpose of this endpoint is to allow clients which deal with arbitrary URLs
 * (for example, allow a user to enter a URL) to validate whether the URL actually points to a RFS API.
 */
@RestController
@RequestMapping("/")
public class VersionController {
    @Autowired
    private RfsServerVersion serverVersion;

    @RequestMapping(method = RequestMethod.GET)
    public Version getVersion(Authentication auth) {
        List<String> access = new ArrayList<>();
        for (GrantedAuthority authority : auth.getAuthorities()) {
            access.add(authority.getAuthority());
        }

        Version version = new Version();
        version.setId(serverVersion.getId());
        version.setVersion(serverVersion.getFullVersion());
        version.setAccess(access);
        return version;
    }
}
