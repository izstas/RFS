package me.izstas.rfs.server.service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import me.izstas.rfs.server.config.security.RfsAccess;
import me.izstas.rfs.model.*;

/**
 * This service provides ability to get metadata from files and apply new metadata to them.
 */
@Service
public class MetadataService {
    @Autowired
    private PathService pathService;

    /**
     * Gets the metadata from the specified path.
     * Requires read access.
     * @param path the user path (path relative to user's root)
     * @return the metadata
     */
    @Secured(RfsAccess.READ)
    public Metadata getMetadataFromUserPath(String path) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.notExists(resolvedPath)) {
            throw new NonexistentPathException();
        }

        return createMetadata(resolvedPath);
    }

    /**
     * Applies the metadata to the specified path.
     * Requires write access.
     * @param path the user path (path relative to user's root)
     * @param metadata the metadata to apply
     */
    @Secured(RfsAccess.WRITE)
    public void applyMetadataToUserPath(String path, Metadata metadata) {
        Path resolvedPath = pathService.resolveUserPath(path);

        // Create the file/directory if doesn't exist
        if (metadata instanceof DirectoryMetadata) {
            if (Files.exists(resolvedPath) && Files.isRegularFile(resolvedPath)) {
                throw new IncompatibleMetadataException("The path is a file");
            }

            if (Files.notExists(resolvedPath)) {
                PathUtils.createDirectories(resolvedPath, false);
            }
        }

        if (metadata instanceof FileMetadata) {
            if (Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)) {
                throw new IncompatibleMetadataException("The path is a directory");
            }

            if (Files.notExists(resolvedPath)) {
                PathUtils.createDirectories(resolvedPath, true);

                try {
                    Files.createFile(resolvedPath);
                }
                catch (IOException e) {
                    throw PathUtils.wrapException(e, "Can't create the file");
                }
            }
        }

        // Handle renaming
        if (metadata.getName() != null) {
            Path targetName = Paths.get(metadata.getName());
            if (targetName.isAbsolute()) {
                throw new BadMetadataException("The target path (name) must not be absolute");
            }

            Path targetPath = resolvedPath.resolveSibling(targetName);
            pathService.validateUserPath(targetPath);

            if (!targetPath.equals(resolvedPath)) {
                if (Files.exists(targetPath)) {
                    throw new BadMetadataException("The target path (name) already exists");
                }

                PathUtils.createDirectories(targetPath, true);

                try {
                    resolvedPath = Files.move(resolvedPath, targetPath);
                }
                catch (IOException e) {
                    throw PathUtils.wrapException(e, "Can't move the file/directory");
                }
            }
        }

        // Apply attributes
        applyBasicAttributes(resolvedPath, metadata);

        if (metadata.getAttributes() != null && metadata.getAttributes() instanceof PosixAttributes) {
            applyPosixAttributes(resolvedPath, metadata);
        }

        if (metadata.getAttributes() != null && metadata.getAttributes() instanceof DosAttributes) {
            applyDosAttributes(resolvedPath, metadata);
        }
    }

    private void applyBasicAttributes(Path path, Metadata metadata) {
        BasicFileAttributeView basicAttrsView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        try {
            basicAttrsView.setTimes(
                    metadata.getLastModificationTime() != null ? FileTime.fromMillis(metadata.getLastModificationTime()) : null,
                    metadata.getLastAccessTime() != null ? FileTime.fromMillis(metadata.getLastAccessTime()) : null,
                    metadata.getCreationTime() != null ? FileTime.fromMillis(metadata.getCreationTime()) : null);
        }
        catch (IOException e) {
            throw PathUtils.wrapException(e, "Can't apply basic file attributes");
        }
    }

    private void applyPosixAttributes(Path path, Metadata metadata) {
        PosixAttributes attrs = (PosixAttributes) metadata.getAttributes();

        PosixFileAttributeView posixAttrsView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        if (posixAttrsView != null) {
            try {
                if (attrs.getUser() != null) {
                    posixAttrsView.setOwner(path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(attrs.getUser()));
                }

                if (attrs.getGroup() != null) {
                    posixAttrsView.setGroup(path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName(attrs.getGroup()));
                }

                if (attrs.getPermissions() != null) {
                    posixAttrsView.setPermissions(PosixFilePermissions.fromString(attrs.getPermissions()));
                }
            }
            catch (UserPrincipalNotFoundException e) {
                throw new BadMetadataException("The user/group does not exist", e);
            }
            catch (IOException e) {
                throw PathUtils.wrapException(e, "Can't apply POSIX file attributes");
            }
        }
    }

    private void applyDosAttributes(Path path, Metadata metadata) {
        DosAttributes attrs = (DosAttributes) metadata.getAttributes();

        DosFileAttributeView dosAttrsView = Files.getFileAttributeView(path, DosFileAttributeView.class);
        if (dosAttrsView != null) {
            try {
                if (attrs.getReadOnly() != null) {
                    dosAttrsView.setReadOnly(attrs.getReadOnly());
                }

                if (attrs.getHidden() != null) {
                    dosAttrsView.setHidden(attrs.getHidden());
                }

                if (attrs.getSystem() != null) {
                    dosAttrsView.setSystem(attrs.getSystem());
                }

                if (attrs.getArchive() != null) {
                    dosAttrsView.setArchive(attrs.getArchive());
                }
            }
            catch (IOException e) {
                throw PathUtils.wrapException(e, "Can't apply DOS file attributes");
            }
        }
    }


    private static Metadata createMetadata(Path path) {
        return createMetadata(path, true);
    }

    private static Metadata createMetadata(Path path, boolean includeContentsIfDirectory) {
        return Files.isDirectory(path) ? createDirectoryMetadata(path, includeContentsIfDirectory) : createFileMetadata(path);
    }

    private static FileMetadata createFileMetadata(Path path) {
        FileMetadata metadata = new FileMetadata();
        populateSharedMetadata(metadata, path);

        long size;
        try {
            size = Files.size(path);
        }
        catch (IOException e) {
            throw PathUtils.wrapException(e, "Can't get file size");
        }

        metadata.setSize(size);
        return metadata;
    }

    private static DirectoryMetadata createDirectoryMetadata(Path path, boolean includeContents) {
        DirectoryMetadata metadata = new DirectoryMetadata();
        populateSharedMetadata(metadata, path);

        if (includeContents) {
            List<Metadata> contents = new ArrayList<>();
            try (DirectoryStream<Path> subPaths = Files.newDirectoryStream(path)) {
                for (Path subPath : subPaths) {
                    contents.add(createMetadata(subPath, false));
                }
            }
            catch (IOException e) {
                throw PathUtils.wrapException(e, "Can't get directory contents");
            }

            metadata.setContents(contents);
        }

        return metadata;
    }

    private static void populateSharedMetadata(Metadata metadata, Path path) {
        if (path.getNameCount() > 0) {
            metadata.setName(path.getFileName().toString());
        }

        BasicFileAttributeView basicAttrsView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        BasicFileAttributes basicAttrs;
        try {
            basicAttrs = basicAttrsView.readAttributes();
        }
        catch (IOException e) {
            throw PathUtils.wrapException(e, "Can't get basic file attributes");
        }

        metadata.setCreationTime(basicAttrs.creationTime().toMillis());
        metadata.setLastAccessTime(basicAttrs.lastAccessTime().toMillis());
        metadata.setLastModificationTime(basicAttrs.lastModifiedTime().toMillis());

        PosixFileAttributeView posixAttrsView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        if (posixAttrsView != null) {
            PosixFileAttributes posixAttrs;
            try {
                posixAttrs = posixAttrsView.readAttributes();
            }
            catch (IOException e) {
                throw PathUtils.wrapException(e, "Can't get POSIX file attributes");
            }

            PosixAttributes attrs = new PosixAttributes();
            attrs.setUser(posixAttrs.owner().getName());
            attrs.setGroup(posixAttrs.group().getName());
            attrs.setPermissions(PosixFilePermissions.toString(posixAttrs.permissions()));

            metadata.setAttributes(attrs);
        }

        DosFileAttributeView dosAttrsView = Files.getFileAttributeView(path, DosFileAttributeView.class);
        if (dosAttrsView != null && posixAttrsView == null) {
            DosFileAttributes dosAttrs;
            try {
                dosAttrs = dosAttrsView.readAttributes();
            }
            catch (IOException e) {
                throw PathUtils.wrapException(e, "Can't get DOS file attributes");
            }

            DosAttributes attrs = new DosAttributes();
            attrs.setReadOnly(dosAttrs.isReadOnly());
            attrs.setHidden(dosAttrs.isHidden());
            attrs.setSystem(dosAttrs.isSystem());
            attrs.setArchive(dosAttrs.isArchive());

            metadata.setAttributes(attrs);
        }
    }
}
