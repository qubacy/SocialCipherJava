package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import java.io.File;
import java.net.URI;

public class AttachmentContext {
    private static final char C_DIR_DIVIDER = '/';

    public static String getExtensionByFilePath(final String filepath) {
        if (filepath == null) return null;
        if (filepath.isEmpty()) return null;

        if (!filepath.contains(".")) return new String("");

        File file = new File(filepath);
        String fileName = file.getName();

        return getExtensionByFileName(fileName);
    }

    public static String getExtensionByFileName(final String filename) {
        return getPartOfFileName(filename, true);
    }

    public static String getAttachmentIdByFileName(final String filename) {
        return getPartOfFileName(filename, false);
    }

    private static String getPartOfFileName(
            final String filename,
            final boolean isLast)
    {
        if (filename == null) return null;
        if (filename.isEmpty()) return null;

        if (!filename.contains(".")) return filename;

        String[] attachmentFileNameParts = filename.split("\\.");

        if (isLast)
            return attachmentFileNameParts[attachmentFileNameParts.length - 1];

        return attachmentFileNameParts[0];
    }

    public static URI getURIByFilePath(final String filePath) {
        if (filePath == null) return null;

        URI uri = null;

        try {
            uri = new URI(filePath);

        } catch (Throwable e) {
            e.printStackTrace();

            return null;
        }

        return uri;
    }

    public static String getFileNameByURI(final URI uri) {
        if (uri == null) return null;

        String filePath = uri.getPath();
        int lastDividerIndex = filePath.lastIndexOf(C_DIR_DIVIDER);

        if (lastDividerIndex < 0) return filePath;

        return filePath.substring(lastDividerIndex + 1);
    }
}
