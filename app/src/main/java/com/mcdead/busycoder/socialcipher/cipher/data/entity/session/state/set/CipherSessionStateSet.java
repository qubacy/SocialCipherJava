package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.set;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionStateOverall;

public class CipherSessionStateSet implements CipherSessionState {
    final private CipherKey m_cipherKey;

    public CipherSessionStateSet(
            final CipherKey cipherKey)
    {
        m_cipherKey = cipherKey;
    }

    public CipherKey getCipherKey() {
        return m_cipherKey;
    }

    @Override
    public CipherSessionStateOverall getOverallState() {
        return CipherSessionStateOverall.SET;
    }
}
