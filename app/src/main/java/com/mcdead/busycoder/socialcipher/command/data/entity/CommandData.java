package com.mcdead.busycoder.socialcipher.command.data.entity;

import com.mcdead.busycoder.socialcipher.command.CommandCategory;

import java.util.List;

public class CommandData {
    final private CommandCategory m_category;
    final private List<Long> m_receiverPeerIdList;

    final private String m_specificCommandTypeData;

    public CommandData(
            final CommandCategory category,
            final List<Long> receiverPeerIdList,
            final String specificCommandTypeData)
    {
        m_category = category;
        m_receiverPeerIdList = receiverPeerIdList;

        m_specificCommandTypeData = specificCommandTypeData;
    }

    public CommandCategory getCategory() {
        return m_category;
    }

    public List<Long> getReceiverPeerIdList() {
        return m_receiverPeerIdList;
    }

    public String getSpecificCommandTypeData() {
        return m_specificCommandTypeData;
    }
}
