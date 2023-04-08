package com.mcdead.busycoder.socialcipher.dialoglist;

import androidx.lifecycle.ViewModel;

public class DialogsViewModel extends ViewModel {
    private long m_currentChatId = 0;

    public DialogsViewModel() {
        super();
    }

    public void setCurrentChatId(final long chatId) {
        m_currentChatId = chatId;
    }

    public long getCurrentChatId() {
        return m_currentChatId;
    }
}
