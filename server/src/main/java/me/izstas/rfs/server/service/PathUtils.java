package me.izstas.rfs.server.service;

import java.io.IOException;
import java.nio.file.*;

import me.izstas.rfs.server.RfsException;

class PathUtils {
    static RfsException wrapException(IOException e, String message) {
        if (e instanceof AccessDeniedException) {
            return new InaccessiblePathException(e);
        }

        if (e instanceof NoSuchFileException) {
            return new NonexistentPathException(e);
        }

        return new RfsException(message, e);
    }

    static void createDirectories(Path path, boolean parent) {
        try {
            Files.createDirectories(parent ? path.getParent() : path);
        }
        catch (FileAlreadyExistsException | NoSuchFileException e) { // While combination of these two may look weird,
                                                                     // on Windows we seem to get the latter one
                                                                     // if the problem is caused not by the immediate parent
                                                                     // but a further ascendant
            throw new NonexistentPathException("The path contains a file (not a directory) as one of the ascendants", e);
        }
        catch (IOException e) {
            throw wrapException(e, parent ? "Can't create ascendant directories" : "Can't create directories");
        }
    }


    private PathUtils() {
        throw new AssertionError();
    }
}
