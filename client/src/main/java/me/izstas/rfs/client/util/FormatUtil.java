package me.izstas.rfs.client.util;

import java.text.DateFormat;
import java.util.Date;

import me.izstas.rfs.model.Attributes;
import me.izstas.rfs.model.DosAttributes;
import me.izstas.rfs.model.PosixAttributes;

/**
 * An utility class with static methods for formatting various values.
 */
public final class FormatUtil {
    /**
     * Formats a file size.
     * @param size the file size in bytes
     * @return formatted file size, e.g. 1 KB, 2 MB, etc
     */
    public static String formatSize(Long size) {
        if (size == null) {
            return null;
        }

        // Based on code by aioobe, http://stackoverflow.com/a/3758880
        int unit = 1024;
        if (size < unit) {
            return String.format("%d B", size);
        }

        int exp = (int) (Math.log(size) / Math.log(unit));
        return String.format("%.2f %cB", size / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }

    /**
     * Formats a date.
     * @param date the date
     * @return formatted date
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }

        return DateFormat.getDateTimeInstance().format(date);
    }

    /**
     * Formats file attributes.
     * @param attributes the attributes
     * @return string representation of the attributes
     */
    public static String formatAttributes(Attributes attributes) {
        if (attributes instanceof PosixAttributes) {
            return formatPosixAttributes((PosixAttributes) attributes);
        }

        if (attributes instanceof DosAttributes) {
            return formatDosAttributes((DosAttributes) attributes);
        }

        return null;
    }

    private static String formatPosixAttributes(PosixAttributes attributes) {
        return String.format("%s:%s %s", attributes.getUser(), attributes.getGroup(), attributes.getPermissions());
    }

    private static String formatDosAttributes(DosAttributes attributes) {
        StringBuilder result = new StringBuilder();

        if (attributes.getReadOnly()) {
            result.append('R');
        }
        if (attributes.getHidden()) {
            result.append('H');
        }
        if (attributes.getSystem()) {
            result.append('S');
        }
        if (attributes.getSystem()) {
            result.append('A');
        }

        return result.toString();
    }


    private FormatUtil() {
        throw new AssertionError();
    }
}
