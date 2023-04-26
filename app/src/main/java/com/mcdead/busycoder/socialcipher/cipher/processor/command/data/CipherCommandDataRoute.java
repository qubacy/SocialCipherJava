package com.mcdead.busycoder.socialcipher.cipher.processor.command.data;

public class CipherCommandDataRoute extends CipherCommandData {
    final private int m_routeId;
    final private byte[] m_data;

    private CipherCommandDataRoute(
            final int routeId,
            final byte[] data)
    {
        m_routeId = routeId;
        m_data = data;
    }

    public static CipherCommandDataRoute getInstance(
            final int routeId,
            final byte[] data)
    {
        if (routeId < 0) return null;
        if (data == null) return null;
        if (data.length <= 0) return null;

        return new CipherCommandDataRoute(routeId, data);
    }

    public int getRouteId() {
        return m_routeId;
    }

    public byte[] getData() {
        return m_data;
    }
}
