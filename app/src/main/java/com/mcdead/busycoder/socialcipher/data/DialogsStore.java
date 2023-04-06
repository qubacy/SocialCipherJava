package com.mcdead.busycoder.socialcipher.data;

import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;

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

public class DialogsStore {
    private static DialogsStore s_instance = null;

    private volatile List<DialogEntity> m_dialogs = null;

    private DialogsStore() {
        m_dialogs = new LinkedList<>();
    }

    public static DialogsStore getInstance() {
        if (s_instance == null)
            s_instance = new DialogsStore();

        return s_instance;
    }

    public List<DialogEntity> getDialogs() {
        synchronized (m_dialogs) {
            return new ArrayList<DialogEntity>(m_dialogs);
        }
    }

    public DialogEntity getDialogByPeerId(final long peerId) {
        if (peerId == 0) return null;

        for (final DialogEntity dialog : m_dialogs)
            if (dialog.getDialogId() == peerId)
                return dialog;

        return null;
    }

    public DialogEntity getDialogByIndex(final int index) {
        if (index < 0 || m_dialogs.size() <= index)
            return null;

        synchronized (m_dialogs) {
            return m_dialogs.get(index);
        }
    }

    public boolean addNewMessage(
            final MessageEntity message,
            final long chatId)
    {
        if (message == null) return false;

        synchronized (m_dialogs) {
            DialogEntity dialog = getDialogByPeerId(chatId);

            if (dialog == null) return false;

            if (!dialog.addMessage(message))
                return false;

            m_dialogs.remove(dialog);
            m_dialogs.add(0, dialog);
        }

        return true;
    }

    public boolean removeMessage(final long peerId,
                                 final long messageId)
    {
        if (messageId == 0) return false;

        synchronized (m_dialogs) {
            DialogEntity dialog = getDialogByPeerId(peerId);

            if (dialog == null) return false;

            if (!dialog.removeMessageById(messageId))
                return false;

            // should it take into account a dialogs' order?
        }

        return true;
    }

    public boolean addDialog(final DialogEntity dialog) {
        if (dialog == null) return false;

        synchronized (m_dialogs) {
            m_dialogs.add(dialog);
        }

        return true;
    }

    public boolean removeDialog(final long peerId) {
        if (peerId == 0) return false;

        synchronized (m_dialogs) {
            for (final DialogEntity dialog : m_dialogs)
                if (dialog.getDialogId() == peerId)
                    return m_dialogs.remove(peerId);
        }

        return false;
    }
}
