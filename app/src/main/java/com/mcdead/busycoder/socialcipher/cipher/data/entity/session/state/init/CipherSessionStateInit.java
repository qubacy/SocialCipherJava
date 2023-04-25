package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionStateOverall;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.CipherSessionState;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;

import java.util.List;

public class CipherSessionStateInit implements CipherSessionState {
    final private List<CipherSessionInitRoute> m_routeList;

    protected CipherSessionStateInit(
            final List<CipherSessionInitRoute> routeList)
    {
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
