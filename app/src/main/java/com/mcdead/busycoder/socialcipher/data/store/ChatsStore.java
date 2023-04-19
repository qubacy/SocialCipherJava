package com.mcdead.busycoder.socialcipher.data.store;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.chat.ChatEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
*
* MODIFYING CAN BE CONDUCTED BY:
* UI (obscuring = deleting),
* Network (new dialog created = adding, deleting);
*
*/

public class ChatsStore {
    private static ChatsStore s_instance = null;

    private volatile List<ChatEntity> m_chatList = null;

    private ChatsStore() {
        m_chatList = new LinkedList<>();
    }

    public static ChatsStore getInstance() {
        if (s_instance == null)
            s_instance = new ChatsStore();

        return s_instance;
    }

    public List<ChatEntity> getChatList() {
        synchronized (m_chatList) {
            return new ArrayList<ChatEntity>(m_chatList);
        }
    }

    public ChatEntity getChatByPeerId(final long peerId) {
        if (peerId == 0) return null;

        for (final ChatEntity dialog : m_chatList)
            if (dialog.getDialogId() == peerId)
                return dialog;

        return null;
    }

    public ChatEntity getChatByIndex(final int index) {
        if (index < 0 || m_chatList.size() <= index)
            return null;

        synchronized (m_chatList) {
            return m_chatList.get(index);
        }
    }

    public boolean setMessageAttachments(
            final List<AttachmentEntityBase> attachmentsList,
            final long chatId,
            final long messageId)
    {
        if (attachmentsList == null) return false;

        synchronized (m_chatList) {
            ChatEntity dialog = getChatByPeerId(chatId);

            if (dialog == null) return false;

            MessageEntity message = dialog.getMessageById(messageId);

            if (message == null) return false;

            if (!message.setAttachments(attachmentsList))
                return false;
        }

        return true;
    }

    public boolean addNewMessage(
            final MessageEntity message,
            final long chatId)
    {
        if (message == null) return false;

        synchronized (m_chatList) {
            ChatEntity dialog = getChatByPeerId(chatId);

            if (dialog == null) return false;

            if (!dialog.addMessage(message))
                return false;

            m_chatList.remove(dialog);
            m_chatList.add(0, dialog);
        }

        return true;
    }

    public boolean removeMessage(final long peerId,
                                 final long messageId)
    {
        if (messageId == 0) return false;

        synchronized (m_chatList) {
            ChatEntity dialog = getChatByPeerId(peerId);

            if (dialog == null) return false;

            if (!dialog.removeMessageById(messageId))
                return false;

            // should it take into account a dialogs' order?
        }

        return true;
    }

    public boolean addChat(final ChatEntity dialog) {
        if (dialog == null) return false;

        synchronized (m_chatList) {
            m_chatList.add(dialog);
        }

        return true;
    }

    public boolean removeChat(final long peerId) {
        if (peerId == 0) return false;

        synchronized (m_chatList) {
            for (final ChatEntity dialog : m_chatList)
                if (dialog.getDialogId() == peerId)
                    return m_chatList.remove(peerId);
        }

        return false;
    }

    public void clean() {
        synchronized (m_chatList) {
            m_chatList.clear();
        }
    }
}
