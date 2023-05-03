package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data;

import java.util.List;

public class CipherSessionInitRoute {
    public static final int C_NO_NEXT_SIDE_ID = -0b01;
    public static final int C_INCORRECT_SIDE_ID_PROVIDED = -0b10;

    final private List<Integer> m_sideIdList;

    private CipherSessionInitRoute(
            final List<Integer> sideIdList)
    {
        m_sideIdList = sideIdList;
    }

    public static CipherSessionInitRoute getInstance(
            final List<Integer> sideIdList)
    {
        if (sideIdList == null) return null;
        if (sideIdList.isEmpty()) return null;

        return new CipherSessionInitRoute(sideIdList);
    }

    public int getNextSideId(final int sideId) {
        if (!m_sideIdList.contains(sideId))
            return C_INCORRECT_SIDE_ID_PROVIDED;

        for (int i = 0; i < m_sideIdList.size() - 1; ++i) {
            int curSideId = m_sideIdList.get(i);

            if (curSideId == sideId)
                return m_sideIdList.get(i + 1);
        }

        return C_NO_NEXT_SIDE_ID;
    }
}
