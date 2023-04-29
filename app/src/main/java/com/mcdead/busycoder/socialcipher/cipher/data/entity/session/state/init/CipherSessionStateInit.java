package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.CipherContext;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionStateOverall;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.utility.CipherKeyUtility;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
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

    public PublicKey getPublicKey() {
        return m_publicKey;
    }

    public CipherSessionInitRoute getRouteById(final int routeId) {
        if (routeId < 0 || routeId > m_routeList.size())
            return null;

        return m_routeList.get(routeId);
    }

    public byte[] processKeyData(
            final byte[] publicSideKeyBytes,
            final boolean isLastStage)
    {
        PublicKey publicKey =
                CipherKeyUtility.generatePublicKeyWithBytes(
                        CipherContext.C_ALGORITHM,
                        publicSideKeyBytes);

        if (publicKey == null) return null;

        Key sideProcessedData = null;

        try {
            sideProcessedData = m_keyAgreement.doPhase(publicKey, isLastStage);

        } catch (InvalidKeyException e) {
            e.printStackTrace();

            return null;
        }

        if (isLastStage)
            m_keyAgreement.generateSecret();
        else
            sideProcessedData.getEncoded();

        return null;
    }

    public boolean isInitCompleted() {
        return m_routeList.isEmpty();
    }

    @Override
    public CipherSessionStateOverall getOverallState() {
        return CipherSessionStateOverall.INIT;
    }
}
