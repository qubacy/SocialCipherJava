package com.mcdead.busycoder.socialcipher.client.data.entity.attachment;

import java.io.File;
import java.net.URI;

public class AttachmentContext {
    public static String getExtensionByFilePath(final String filepath) {
        if (filepath == null) return null;
        if (filepath.isEmpty()) return null;

        if (!filepath.contains(".")) return new String("");

        File file = new File(filepath);
        String fileName = file.getName();

        return getExtensionByFileName(fileName);
    }

    public static String getExtensionByFileName(final String filename) {
        return getPartOfFileName(filename, 1);
    }

    public static String getAttachmentIdByFileName(final String filename) {
        return getPartOfFileName(filename, 0);
    }

    private static String getPartOfFileName(
            final String filename,
            final int partIndex)
    {
        if (filename == null) return null;
        if (filename.isEmpty()) return null;

        if (!filename.contains(".")) return filename;

        String[] attachmentFileNameParts = filename.split("\\.");

        if (attachmentFileNameParts.length <= partIndex)
            return null;

        return attachmentFileNameParts[partIndex];
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
}
