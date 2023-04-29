package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.set;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionStateOverall;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;

public class CipherSessionStateSet implements CipherSessionState {
    final private CiphererBase m_cipherer;

    private CipherSessionStateSet(
            final CiphererBase cipherer)
    {
        m_cipherer = cipherer;
    }

    public static CipherSessionStateSet getInstance(
            final CiphererBase cipherer)
    {
        if (cipherer == null) return null;

        return new CipherSessionStateSet(cipherer);
    }

    public CiphererBase getCipherer() {
        return m_cipherer;
    }

    @Override
    public CipherSessionStateOverall getOverallState() {
        return CipherSessionStateOverall.SET;
    }
}
