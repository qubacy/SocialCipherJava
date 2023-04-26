package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionStateOverall;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.KeyAgreement;

public class CipherSessionStateInit implements CipherSessionState {
    final private PrivateKey m_privateKey;
    final private PublicKey m_publicKey;

    final private KeyAgreement m_keyAgreement;

    final private List<CipherSessionInitRoute> m_routeList;

    protected CipherSessionStateInit(
            final PrivateKey privateKey,
            final PublicKey publicKey,
            final KeyAgreement keyAgreement,
            final List<CipherSessionInitRoute> routeList)
    {
        m_privateKey = privateKey;
        m_publicKey = publicKey;

        m_keyAgreement = keyAgreement;
        m_routeList = routeList;
    }

    public boolean removeRoute(
            final int sideIdSender,
            final int sideIdReceiver)
    {
        if (sideIdSender <= 0 || sideIdReceiver <= 0)
            return false;

        for (final CipherSessionInitRoute route : m_routeList)
            if (route.getSideIdSender() == sideIdSender
             && route.getSideIdReceiver() == sideIdReceiver)
            {
                return m_routeList.remove(route);
            }

        return true;
    }

    public boolean isInitCompleted() {
        return m_routeList.isEmpty();
    }



    @Override
    public CipherSessionStateOverall getOverallState() {
        return CipherSessionStateOverall.INIT;
    }
}
