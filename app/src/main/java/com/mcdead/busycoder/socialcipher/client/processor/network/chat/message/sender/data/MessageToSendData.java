package com.mcdead.busycoder.socialcipher.client.processor.network.chat.message.sender.data;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;

import java.util.List;

public class MessageToSendData {
    final protected long m_chatId;
    final protected String m_text;
    final protected List<AttachmentData> m_uploadingAttachmentList;

    protected MessageToSendData(
            final long chatId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList)
    {
        m_chatId = chatId;
        m_text = text;
        m_uploadingAttachmentList = uploadingAttachmentList;
    }

    public static MessageToSendData getInstance(
            final long chatId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList)
    {
        ChatIdChecker chatIdChecker = ChatIdCheckerGenerator.generateChatIdChecker();

        if (chatIdChecker == null || (text == null && uploadingAttachmentList == null))
            return null;
        if (!chatIdChecker.isValid(chatId))
            return null;

        if (text != null) {
            if (text.isEmpty() && uploadingAttachmentList.isEmpty()) return null;
        } else {
            if (uploadingAttachmentList.isEmpty()) return null;
        }

        return new MessageToSendData(chatId, text, uploadingAttachmentList);
    }

    public long getChatId() {
        return m_chatId;
    }

    public String getText() {
        return m_text;
    }

    public List<AttachmentData> getUploadingAttachmentList() {
        return m_uploadingAttachmentList;
    }
}
