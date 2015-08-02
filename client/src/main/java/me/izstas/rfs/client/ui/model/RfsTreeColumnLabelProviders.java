package me.izstas.rfs.client.ui.model;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import me.izstas.rfs.client.ui.Messages;
import me.izstas.rfs.client.util.FormatUtil;
import me.izstas.rfs.model.DirectoryMetadata;
import me.izstas.rfs.model.FileMetadata;
import me.izstas.rfs.model.Metadata;

public final class RfsTreeColumnLabelProviders {
    /**
     * A label provider for the "Name" column.
     */
    public static final class Name extends ColumnLabelProvider {
        private Image directoryImage;
        private boolean directoryImageCached;
        private Image defaultFileImage;
        private boolean defaultFileImageCached;
        private final Map<String, Image> fileImages = new HashMap<>();

        @Override
        public void dispose() {
            if (directoryImage != null) {
                directoryImage.dispose();
            }

            if (defaultFileImage != null) {
                defaultFileImage.dispose();
            }

            for (Image fileImage : fileImages.values()) {
                if (fileImage != null) {
                    fileImage.dispose();
                }
            }
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof RfsMetadataNode) {
                Metadata metadata = ((RfsMetadataNode) element).getMetadata();

                if (metadata instanceof DirectoryMetadata) {
                    return getDirectoryImage();
                }

                int extensionIndex = metadata.getName().lastIndexOf('.');
                if (extensionIndex != -1) {
                    String extension = metadata.getName().substring(extensionIndex);

                    Image image = getFileImage(extension);
                    if (image != null) {
                        return image;
                    }
                }

                return getDefaultFileImage();
            }

            return null;
        }

        private Image getDirectoryImage() {
            if (!directoryImageCached) {
                for (Program program : Program.getPrograms()) {
                    if (program.getName().equalsIgnoreCase("folder")) {
                        if (program.getImageData() != null) {
                            directoryImage = new Image(Display.getCurrent(), program.getImageData());
                        }
                        break;
                    }
                }

                if (directoryImage == null) {
                    directoryImage = new Image(Display.getCurrent(), getClass().getResourceAsStream("/me/izstas/rfs/client/ui/images/directory.png"));
                }

                directoryImageCached = true;
            }

            return directoryImage;
        }

        private Image getDefaultFileImage() {
            if (!defaultFileImageCached) {
                // No way to get a platform-specific icon?
                defaultFileImage = new Image(Display.getCurrent(), getClass().getResourceAsStream("/me/izstas/rfs/client/ui/images/file.png"));

                defaultFileImageCached = true;
            }

            return defaultFileImage;
        }

        private Image getFileImage(String extension) {
            if (!fileImages.containsKey(extension)) {
                Program program = Program.findProgram(extension);
                if (program != null && program.getImageData() != null) {
                    fileImages.put(extension, new Image(Display.getCurrent(), program.getImageData()));
                }
                else {
                    fileImages.put(extension, null);
                }
            }

            return fileImages.get(extension);
        }

        @Override
        public String getText(Object element) {
            if (element instanceof RfsMetadataNode) {
                return ((RfsMetadataNode) element).getMetadata().getName();
            }
            if (element instanceof RfsDummyRetrievingNode) {
                return Messages.MainWindow_tree_loading;
            }
            if (element instanceof RfsDummyErrorNode) {
                return String.format(Messages.MainWindow_tree_error, Messages.getForException(((RfsDummyErrorNode) element).getCause()));
            }

            return null;
        }
    }

    /**
     * A label provider for the "Size" column.
     */
    public static final class Size extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof RfsMetadataNode) {
                Metadata metadata = ((RfsMetadataNode) element).getMetadata();
                if (metadata instanceof FileMetadata) {
                    return FormatUtil.formatSize(((FileMetadata) metadata).getSize());
                }
            }

            return null;
        }
    }

    /**
     * A label provider for the "Date modified" column.
     */
    public static final class DateModified extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof RfsMetadataNode) {
                Metadata metadata = ((RfsMetadataNode) element).getMetadata();
                return FormatUtil.formatDate(metadata.getLastModificationTime());
            }

            return null;
        }
    }

    /**
     * A label provider for the "Attributes" column.
     */
    public static final class Attributes extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof RfsMetadataNode) {
                Metadata metadata = ((RfsMetadataNode) element).getMetadata();
                return FormatUtil.formatAttributes(metadata.getAttributes());
            }

            return null;
        }
    }


    private RfsTreeColumnLabelProviders() {
        throw new AssertionError();
    }
}
