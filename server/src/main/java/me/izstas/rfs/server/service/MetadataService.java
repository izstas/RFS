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
import me.izstas.rfs.server.model.*;

@Service
public class MetadataService {
    @Autowired
    private PathService pathService;

    @Secured(RfsAccess.READ)
    public Metadata getMetadataFromUserPath(String path) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.notExists(resolvedPath)) {
            throw new NonexistentPathException();
        }

        return createMetadata(resolvedPath);
    }

    @Secured(RfsAccess.WRITE)
    public void applyMetadataToUserPath(String path, Metadata metadata) {
        Path resolvedPath = pathService.resolveUserPath(path);

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


        if (metadata.getName() != null) {
            Path targetPath = resolvedPath.resolveSibling(metadata.getName());
            pathService.validateUserPath(targetPath);

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

        BasicFileAttributeView basicAttrsView = Files.getFileAttributeView(resolvedPath, BasicFileAttributeView.class);
        try {
            basicAttrsView.setTimes(
                    metadata.getLastModificationTime() != null ? FileTime.fromMillis(metadata.getLastModificationTime()) : null,
                    metadata.getLastAccessTime() != null ? FileTime.fromMillis(metadata.getLastAccessTime()) : null,
                    metadata.getCreationTime() != null ? FileTime.fromMillis(metadata.getCreationTime()) : null);
        }
        catch (IOException e) {
            throw PathUtils.wrapException(e, "Can't apply basic file attributes");
        }

        if (metadata.getAttributes() != null && metadata.getAttributes() instanceof PosixAttributes) {
            PosixAttributes attrs = (PosixAttributes) metadata.getAttributes();

            PosixFileAttributeView posixAttrsView = Files.getFileAttributeView(resolvedPath, PosixFileAttributeView.class);
            if (posixAttrsView != null) {
                try {
                    if (attrs.getUser() != null) {
                        posixAttrsView.setOwner(resolvedPath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(attrs.getUser()));
                    }

                    if (attrs.getGroup() != null) {
                        posixAttrsView.setGroup(resolvedPath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName(attrs.getGroup()));
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

        if (metadata.getAttributes() != null && metadata.getAttributes() instanceof DosAttributes) {
            DosAttributes attrs = (DosAttributes) metadata.getAttributes();

            DosFileAttributeView dosAttrsView = Files.getFileAttributeView(resolvedPath, DosFileAttributeView.class);
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
