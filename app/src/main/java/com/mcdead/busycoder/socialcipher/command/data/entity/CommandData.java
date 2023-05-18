package com.mcdead.busycoder.socialcipher.command.data.entity;

import com.mcdead.busycoder.socialcipher.command.CommandCategory;

import java.util.List;
import java.util.Objects;

public class CommandData {
    final private CommandCategory m_category;
    final private long m_chatId;
    final private List<Long> m_receiverPeerIdList;

    final private String m_specificCommandTypeData;

    public static CommandData getInstance(
            final CommandCategory category,
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String specificCommandTypeData)
    {
        if (category == null || chatId == 0)
            return null;

        if (receiverPeerIdList != null) {
            for (final Long receiverPeerId : receiverPeerIdList) {
                if (receiverPeerId == null) return null;
                if (receiverPeerId == 0) return null;
            }
        }

        return new CommandData(
                category,
                chatId,
                receiverPeerIdList,
                specificCommandTypeData);
    }

    private CommandData(
            final CommandCategory category,
            final long chatId,
            final List<Long> receiverPeerIdList,
            final String specificCommandTypeData)
    {
        m_category = category;
        m_chatId = chatId;
        m_receiverPeerIdList = receiverPeerIdList;

        m_specificCommandTypeData = specificCommandTypeData;
    }

    public CommandCategory getCategory() {
        return m_category;
    }

    public long getChatId() {
        return m_chatId;
    }

    public List<Long> getReceiverPeerIdList() {
        return m_receiverPeerIdList;
    }

    public String getSpecificCommandTypeData() {
        return m_specificCommandTypeData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CommandData that = (CommandData) o;

        return m_chatId == that.m_chatId &&
                m_category == that.m_category &&
                Objects.equals(m_receiverPeerIdList, that.m_receiverPeerIdList) &&
                Objects.equals(m_specificCommandTypeData, that.m_specificCommandTypeData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                m_category,
                m_chatId,
                m_receiverPeerIdList,
                m_specificCommandTypeData);
    }
}
