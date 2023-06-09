package com.mcdead.busycoder.socialcipher.client.data.store;

import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;

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

    public ChatEntity getChatById(final long chatId) {
        if (chatId == 0) return null;

        for (final ChatEntity dialog : m_chatList)
            if (dialog.getId() == chatId)
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
            final long messageId,
            final boolean isCiphered)
    {
        if (attachmentsList == null) return false;

        synchronized (m_chatList) {
            ChatEntity chat = getChatById(chatId);

            if (chat == null) return false;

            MessageEntity message = chat.getMessageById(messageId);

            if (message == null) return false;

            if (!message.setAttachments(attachmentsList))
                return false;

            if (isCiphered) message.setCiphered();
        }

        return true;
    }

    public boolean addNewMessage(
            final MessageEntity message,
            final long chatId)
    {
        if (message == null) return false;

        synchronized (m_chatList) {
            ChatEntity chat = getChatById(chatId);

            if (chat == null) return false;

            if (!chat.addMessage(message))
                return false;

            m_chatList.remove(chat);
            m_chatList.add(0, chat);
        }

        return true;
    }

    public boolean removeMessage(
            final long chatId,
            final long messageId)
    {
        if (messageId == 0) return false;

        synchronized (m_chatList) {
            ChatEntity dialog = getChatById(chatId);

            if (dialog == null) return false;

            if (!dialog.removeMessageById(messageId))
                return false;

            // should it take into account a dialogs' order?
        }

        return true;
    }

    public boolean addChat(final ChatEntity chat) {
        if (chat == null) return false;

        synchronized (m_chatList) {
            m_chatList.add(chat);
        }

        return true;
    }

    public boolean removeChat(final long chatId) {
        if (chatId == 0) return false;

        synchronized (m_chatList) {
            for (final ChatEntity dialog : m_chatList)
                if (dialog.getId() == chatId)
                    return m_chatList.remove(dialog);
        }

        return false;
    }

    public void clean() {
        synchronized (m_chatList) {
            m_chatList.clear();
        }
    }
}
