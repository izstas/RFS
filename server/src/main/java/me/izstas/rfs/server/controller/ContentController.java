package me.izstas.rfs.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.izstas.rfs.server.config.web.PathWithinPattern;

import me.izstas.rfs.server.service.ContentService;

@RestController
@RequestMapping("/content/**")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @RequestMapping(method = RequestMethod.GET)
    public Resource getContent(@PathWithinPattern String path) {
        return contentService.getContentFromUserPath(path);
    }
}
