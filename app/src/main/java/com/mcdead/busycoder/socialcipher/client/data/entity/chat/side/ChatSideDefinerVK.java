package com.mcdead.busycoder.socialcipher.client.data.entity.chat.side;

public class ChatSideDefinerVK implements ChatSideDefiner {
    final private long m_localPeerId;

    public ChatSideDefinerVK(final long localPeerId) {
        m_localPeerId = localPeerId;
    }

    @Override
    public ChatSide defineChatSide(final long chatId) {
        return (m_localPeerId == chatId ? ChatSide.LOCAL : ChatSide.REMOTE);
    }
}
