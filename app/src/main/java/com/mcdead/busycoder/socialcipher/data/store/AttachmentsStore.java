package com.mcdead.busycoder.socialcipher.data.store;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentContext;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityGenerator;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.setting.system.SettingsSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            final HashMap<AttachmentSize, AttachmentData> attachmentSizeDataHashMap)
    {
        if (attachmentSizeDataHashMap == null) return null;
        if (attachmentSizeDataHashMap.isEmpty()) return null;
        if (!attachmentSizeDataHashMap.containsKey(AttachmentSize.STANDARD))
            return null;

        AttachmentData standardAttachmentData =
                attachmentSizeDataHashMap.get(AttachmentSize.STANDARD);

        for (final Map.Entry<AttachmentSize, AttachmentData> attachmentSizeData :
                attachmentSizeDataHashMap.entrySet())
        {
            if (!attachmentSizeData.getValue().isValid()) return null;
        }

        synchronized (m_attachmentsHash) {
            if (m_attachmentsHash.containsKey(standardAttachmentData.getName()))
                return m_attachmentsHash.get(standardAttachmentData.getName());

            HashMap<AttachmentSize, String> attachmentSizeFilePathHashMap = new HashMap<>();

            for (final Map.Entry<AttachmentSize, AttachmentData> attachmentSizeData :
                    attachmentSizeDataHashMap.entrySet())
            {
                String savedFilePath = saveAttachmentToFile(
                        standardAttachmentData.getName(),
                        attachmentSizeData.getKey(),
                        attachmentSizeData.getValue());

                if (savedFilePath == null) return null;

                attachmentSizeFilePathHashMap.put(attachmentSizeData.getKey(), savedFilePath);
            }

            AttachmentEntityBase attachmentEntity = AttachmentEntityGenerator
                    .generateAttachmentByIdAndAttachmentSizeFilePathHashMap(
                            standardAttachmentData.getName(), attachmentSizeFilePathHashMap);

            if (attachmentEntity == null) return null;

            m_attachmentsHash.put(standardAttachmentData.getName(), attachmentEntity);
        }

        return m_attachmentsHash.get(standardAttachmentData.getName());
    }

    private String saveAttachmentToFile(
            final String attachmentId,
            final AttachmentSize attachmentSize,
            final AttachmentData attachmentData)
    {
        SettingsSystem settingsSystem = SettingsSystem.getInstance();

        if (settingsSystem == null) return null;
        if (settingsSystem.getAttachmentsDir() == null) return null;

        String newAttachmentFilePath = generateAttachmentFilePath(
                settingsSystem.getAttachmentsDir(),
                attachmentId,
                attachmentSize,
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

            return null;
        }

        return newAttachmentFilePath;
    }

    private String generateAttachmentFilePath(
            final String dirPath,
            final String attachmentId,
            final AttachmentSize attachmentSize,
            final String attachmentExtension)
    {
        return (dirPath +
                '/' + String.valueOf(attachmentSize.getId()) +
                '/' + attachmentId +
                '.' + attachmentExtension);
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

        HashMap<AttachmentSize, String> attachmentSizeFilePathHashMap = new HashMap<>();

        for (final AttachmentSize attachmentSize : AttachmentSize.values()) {
            File attachmentSizeDir = new File(attachmentsDir, String.valueOf(attachmentSize.getId()));

            if (!attachmentSizeDir.exists() || !attachmentSizeDir.isDirectory()) {
                if (!attachmentSizeDir.mkdirs()) return null;
                if (!attachmentSizeDir.exists()) return null;
            }

            File[] attachmentsFiles = attachmentSizeDir.listFiles();

            for (final File attachmentFile : attachmentsFiles) {
                String attachmentFileName = attachmentFile.getName();

                if (attachmentFileName.isEmpty()) continue;

                String attachmentId = AttachmentContext.getAttachmentIdByFileName(attachmentFileName);

                if (Objects.equals(attachmentId, id)) {
                    attachmentSizeFilePathHashMap.put(attachmentSize, attachmentFile.getPath());

                    break;
                }
            }
        }

        return AttachmentEntityGenerator.generateAttachmentByIdAndAttachmentSizeFilePathHashMap(
                id, attachmentSizeFilePathHashMap);
    }

    public void clean() {
        synchronized (m_attachmentsHash) {
            m_attachmentsHash.clear();
        }
    }
}
