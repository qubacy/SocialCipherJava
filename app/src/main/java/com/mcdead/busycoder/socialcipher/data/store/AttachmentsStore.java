package com.mcdead.busycoder.socialcipher.data.store;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmentdata.AttachmentData;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public AttachmentEntityBase saveAttachment(
            final AttachmentData attachmentData)
    {
        if (attachmentData == null) return null;
        if (!attachmentData.isValid()) return null;

        synchronized (m_attachmentsHash) {
            if (m_attachmentsHash.containsKey(attachmentData.getName()))
                return m_attachmentsHash.get(attachmentData.getName());

            String savedFilePath = saveAttachmentToFile(attachmentData.getName(), attachmentData);

            if (savedFilePath == null) return null;

            AttachmentEntityBase attachmentEntity = AttachmentEntityGenerator
                    .generateAttachmentByIdAndFilePath(attachmentData.getName(), savedFilePath);

            if (attachmentEntity == null) return null;

            m_attachmentsHash.put(attachmentData.getName(), attachmentEntity);
        }

        return m_attachmentsHash.get(attachmentData.getName());
    }

    private String saveAttachmentToFile(
            final String attachmentId,
            final AttachmentData attachmentData)
    {
        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null) return null;
        if (settingsSystem.getAttachmentsDir() == null) return null;

        String newAttachmentFilePath = generateAttachmentFilePath(
                settingsSystem.getAttachmentsDir(),
                attachmentId,
                attachmentData.getExtension());

        File newAttachmentFile = new File(newAttachmentFilePath);

        try {
            if (!newAttachmentFile.getParentFile().exists())
                if (!newAttachmentFile.getParentFile().mkdirs())
                    return null;

            if (!newAttachmentFile.createNewFile())
                return null;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        try (OutputStream out = new FileOutputStream(newAttachmentFile)) {
            out.write(attachmentData.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newAttachmentFilePath;
    }

    private String generateAttachmentFilePath(
            final String dirPath,
            final String attachmentId,
            final String attachmentExtension)
    {
        return (dirPath + '/' + attachmentId + '.' + attachmentExtension);
    }

    public AttachmentEntityBase getAttachmentById(final String id) {
        synchronized (m_attachmentsHash) {
            if (!m_attachmentsHash.containsKey(id))
                return loadAttachmentById(id);
        }

        return m_attachmentsHash.get(id);
    }

    private AttachmentEntityBase loadAttachmentById(final String id) {
        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null) return null;
        if (settingsSystem.getAttachmentsDir() == null) return null;

        File attachmentsDir = new File(settingsSystem.getAttachmentsDir());

        if (!attachmentsDir.exists() || !attachmentsDir.isDirectory()) {
            if (!attachmentsDir.mkdirs()) return null;
            if (!attachmentsDir.exists()) return null;
        }

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

    public void clean() {
        synchronized (m_attachmentsHash) {
            m_attachmentsHash.clear();
        }
    }
}
