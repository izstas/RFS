package me.izstas.rfs.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import me.izstas.rfs.server.config.web.PathWithinPattern;
import me.izstas.rfs.model.Metadata;
import me.izstas.rfs.server.service.MetadataService;

/**
 * This controller provides access to file and directory metadata.
 * @see MetadataService
 */
@RestController
@RequestMapping("/metadata/**")
public class MetadataController {
    @Autowired
    private MetadataService metadataService;

    /**
     * This controller method returns the metadata from the specified path.
     */
    @RequestMapping(method = RequestMethod.GET)
    public Metadata getMetadata(@PathWithinPattern String path) {
        return metadataService.getMetadataFromUserPath(path);
    }

    /**
     * This controller method applies the metadata in the request body to the specified path and responds with 204 No Content in case of success.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void applyMetadata(@PathWithinPattern String path, @RequestBody Metadata metadata) {
        metadataService.applyMetadataToUserPath(path, metadata);
    }
}
