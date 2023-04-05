package com.mcdead.busycoder.socialcipher.data;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityGenerator;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class AttachmentsStore {
    private static AttachmentsStore s_instance = null;

    private HashMap<String, AttachmentEntityBase> m_attachmentsHash = null;

    private AttachmentsStore() {
        m_attachmentsHash = new HashMap<>();
    }

    public static AttachmentsStore getInstance() {
        if (s_instance == null)
            s_instance = new AttachmentsStore();

        return s_instance;
    }

    public boolean addAttachment(final AttachmentEntityBase attachment)
    {
        if (attachment == null) return false;
        if (attachment.getId() == null) return false;
        if (attachment.getId().isEmpty()) return false;

        m_attachmentsHash.put(attachment.getId(), attachment);

        return true;
    }

    public AttachmentEntityBase getAttachmentById(final String id) {
        if (!m_attachmentsHash.containsKey(id))
            return loadAttachmentById(id);

        return m_attachmentsHash.get(id);
    }

    private AttachmentEntityBase loadAttachmentById(final String id) {
        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null) return null;
        if (settingsSystem.getAttachmentsDir() == null) return null;

        File attachmentsDir = new File(settingsSystem.getAttachmentsDir());

        if (!attachmentsDir.exists() || !attachmentsDir.isDirectory())
            return null;

        File[] attachmentsFiles = attachmentsDir.listFiles();

        for (final File attachmentFile : attachmentsFiles) {
            String attachmentFileName = attachmentFile.getName();

            if (attachmentFileName.isEmpty()) continue;

            String attachmentId = AttachmentContext.getAttachmentIdByFileName(attachmentFileName);

            if (Objects.equals(attachmentId, id))
                return AttachmentEntityGenerator.generateAttachmentByIdAndFilePath(
                        attachmentId, attachmentFile.getPath());
        }

        return null;
    }
}
