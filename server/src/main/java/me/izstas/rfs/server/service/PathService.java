package me.izstas.rfs.server.service;

import java.nio.file.Path;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import me.izstas.rfs.server.config.security.RfsUserDetails;

@Service
public class PathService {
    /**
     * Makes sure the provided path is in the user's root.
     * @throws BadPathException if the path is not the user's root
     */
    public void validateUserPath(Path path) {
        Path currentPath = path.toAbsolutePath();
        Path rootPath = getUserRoot().toAbsolutePath();

        do {
            if (currentPath.equals(rootPath)) {
                return;
            }
        }
        while ((currentPath = currentPath.getParent()) != null);

        // If we've reached this line, the provided path is not in the user's root
        throw new BadPathException();
    }

    /**
     * Resolves the provided path against the user's root and validates it with {@link #validateUserPath(Path)}.
     */
    public Path resolveUserPath(String path) {
        Path resolvedPath = getUserRoot().resolve(path);
        validateUserPath(resolvedPath);

        return resolvedPath;
    }


    private Path getUserRoot() {
        return ((RfsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoot();
    }
}
