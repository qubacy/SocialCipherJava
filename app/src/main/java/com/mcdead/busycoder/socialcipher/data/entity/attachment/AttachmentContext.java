package com.mcdead.busycoder.socialcipher.data.entity.attachment;

import java.net.URI;

public class AttachmentContext {
    public static String getExtensionByFilePath(final String filepath) {
        if (filepath == null) return null;
        if (filepath.isEmpty()) return null;

        if (!filepath.contains(".")) return new String("");

        String[] attachmentFilePathParts = filepath.split(".");

        return attachmentFilePathParts[attachmentFilePathParts.length - 1];
    }

    public static String getAttachmentIdByFileName(final String filename) {
        if (filename == null) return null;
        if (filename.isEmpty()) return null;

        if (!filename.contains(".")) return filename;

        String[] attachmentFileNameParts = filename.split(".");

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
}
