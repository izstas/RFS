package me.izstas.rfs.server.service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import me.izstas.rfs.server.model.*;

@Service
public class MetadataService {
    @Autowired
    private PathService pathService;

    @Secured("read")
    public Metadata getMetadataFromUserPath(String path) {
        Path resolvedPath = pathService.resolveUserPath(path);
        if (Files.notExists(resolvedPath)) {
            throw new NonexistentPathException();
        }

        return createMetadata(resolvedPath);
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
            throw new RuntimeException("Can't get file size", e);
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
                throw new RuntimeException("Can't get directory contents", e);
            }

            metadata.setContents(contents);
        }

        return metadata;
    }

    private static void populateSharedMetadata(Metadata metadata, Path path) {
        metadata.setName(path.getFileName().toString());

        BasicFileAttributeView basicAttrsView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        BasicFileAttributes basicAttrs;
        try {
            basicAttrs = basicAttrsView.readAttributes();
        }
        catch (IOException e) {
            throw new RuntimeException("Can't get basic file attributes", e);
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
                throw new RuntimeException("Can't get POSIX file attributes", e);
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
                throw new RuntimeException("Can't get DOS file attributes", e);
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
