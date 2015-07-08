package me.izstas.rfs.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/version")
public class VersionController {
    @Autowired
    private RfsServerVersion version;

    @RequestMapping(method = RequestMethod.GET)
    public Version getVersion() {
        return new Version(version.getId(), version.getFullVersion());
    }
}
