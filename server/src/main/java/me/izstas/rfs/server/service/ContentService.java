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

@Service
public class ContentService {
    @Autowired
    private PathService pathService;

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

    @Secured(RfsAccess.WRITE)
    public void putContentToUserPath(String path, InputStream input) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)) {
            throw new NonexistentPathException("The path is a directory");
        }
        if (Files.exists(resolvedPath) && !Files.isWritable(resolvedPath)) {
            throw new InaccessiblePathException();
        }

        if (resolvedPath.getParent() != null) {
            try {
                Files.createDirectories(resolvedPath.getParent());
            }
            catch (FileAlreadyExistsException | NoSuchFileException e) { // While combination of these two may look weird,
                                                                         // on Windows we seem to get the latter one
                                                                         // if the problem is caused not by the immediate parent
                                                                         // but a further ascendant
                throw new NonexistentPathException("The path contains a file (not a directory) as one of the ascendants", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Can't create ascendant directories", e);
            }
        }

        try (OutputStream output = Files.newOutputStream(resolvedPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            StreamUtils.copy(input, output);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't copy the content to the file", e);
        }
    }
}
