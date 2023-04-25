package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data;

public class CipherSessionInitRoute {
    final private int m_sideIdSender;
    final private int m_sideIdReceiver;
    final private int m_sideIdNextReceiver;

    public CipherSessionInitRoute(
            final int sideIdSender,
            final int sideIdReceiver,
            final int sideIdNextReceiver)
    {
        m_sideIdSender = sideIdSender;
        m_sideIdReceiver = sideIdReceiver;
        m_sideIdNextReceiver = sideIdNextReceiver;
    }

    public int getSideIdSender() {
        return m_sideIdSender;
    }

    public int getSideIdReceiver() {
        return m_sideIdReceiver;
    }

    public int getSideIdNextReceiver() {
        return m_sideIdNextReceiver;
    }
}
