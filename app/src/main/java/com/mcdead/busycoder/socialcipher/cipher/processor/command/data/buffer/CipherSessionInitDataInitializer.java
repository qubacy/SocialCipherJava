package com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer;

import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class CipherSessionInitDataInitializer extends CipherSessionInitData {
    final private List<Long> m_userPeerIdList;
    final private List<ObjectWrapper<Integer>> m_routeCounterList;

    public CipherSessionInitDataInitializer(
            final long startTimeMillisecond,
            final long initializerPeerId,
            final CipherConfiguration cipherConfiguration)
    {
        super(startTimeMillisecond, initializerPeerId, cipherConfiguration);

        m_userPeerIdList = new ArrayList<>();
        m_routeCounterList = new ArrayList<>();

        m_userPeerIdList.add(initializerPeerId); //todo: initializer is added by default;
        m_routeCounterList.add(new ObjectWrapper<>(0));
    }

    public boolean addUser(
            final long userPeerId)
    {
        if (userPeerId == 0) return false;

        for (final Long curUserPeerId : m_userPeerIdList) {
            if (curUserPeerId == userPeerId)
                return false;
        }

        m_userPeerIdList.add(userPeerId);
        m_routeCounterList.add(new ObjectWrapper<>(0));

        return true;
    }

    public void initCounters() {
        int routeCount = m_routeCounterList.size();

        for (int i = 0; i < routeCount - 1; ++i) {
            m_routeCounterList.get(i).setValue(1);
        }
    }

    public List<Long> getUserPeerIdList() {
        return m_userPeerIdList;
    }

    public void addRouteCounterValue(final int routeId) {
        //int routeCounterIndex = routeId - 1;
        int prevValue = m_routeCounterList.get(routeId).getValue(); // todo: exception index = -1 in case of having 3+ users;

        m_routeCounterList.get(routeId).setValue(prevValue + 1);
    }

    public boolean isRouteCounterListFull() {
        int routeCount = m_routeCounterList.size();

        for (final ObjectWrapper<Integer> routeCounterItem : m_routeCounterList) {
            if (routeCounterItem.getValue() != (routeCount - 1))
                return false;
        }

        return true;
    }
}
