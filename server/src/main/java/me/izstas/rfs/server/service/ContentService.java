package me.izstas.rfs.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import me.izstas.rfs.server.config.security.RfsAccess;

/**
 * This service provides ability to read and write file content.
 */
@Service
public class ContentService {
    @Autowired
    private PathService pathService;

    /**
     * Gets the content of the file at the specified path.
     * Requires read access.
     * @param path the user path (path relative to user's root)
     * @return a {@link Resource} which provides the requested content
     */
    @Secured(RfsAccess.READ)
    public Resource getContentFromUserPath(String path) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.notExists(resolvedPath)) {
            throw new NonexistentPathException();
        }
        if (Files.isDirectory(resolvedPath)) {
            throw new NonexistentPathException("The path is a directory");
        }
        if (!Files.isReadable(resolvedPath)) {
            throw new InaccessiblePathException();
        }

        return new PathResource(resolvedPath);
    }

    /**
     * Puts the content to the file at the specified path.
     * @param path the user path (path relative to user's root)
     * @param input the content to put in the form of an {@link InputStream}
     */
    @Secured(RfsAccess.WRITE)
    public void putContentToUserPath(String path, InputStream input) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)) {
            throw new NonexistentPathException("The path is a directory");
        }
        if (Files.exists(resolvedPath) && !Files.isWritable(resolvedPath)) {
            throw new InaccessiblePathException();
        }

        PathUtils.createDirectories(resolvedPath, true);

        try (OutputStream output = Files.newOutputStream(resolvedPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            StreamUtils.copy(input, output);
        }
        catch (IOException e) {
            throw PathUtils.wrapException(e, "Can't copy the content to the file");
        }
    }
}
