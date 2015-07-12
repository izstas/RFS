package me.izstas.rfs.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.izstas.rfs.server.config.web.PathWithinPattern;
import me.izstas.rfs.server.model.Metadata;
import me.izstas.rfs.server.service.MetadataService;

@RestController
@RequestMapping("/metadata/**")
public class MetadataController {
    @Autowired
    private MetadataService metadataService;

    @RequestMapping(method = RequestMethod.GET)
    public Metadata getMetadata(@PathWithinPattern String path) {
        return metadataService.getMetadataFromUserPath(path);
    }
}
