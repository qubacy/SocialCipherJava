package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.messagelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdCheckerGenerator;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {
    final private LayoutInflater m_inflater;
    final private long m_localPeerId;

    private MessageListAdapterCallback m_callback = null;

    private List<MessageEntity> m_messages = null;

    protected MessageListAdapter(
            final LayoutInflater inflater,
            final MessageListAdapterCallback callback,
            final long localPeerId)
    {
        m_inflater = inflater;
        m_callback = callback;
        m_localPeerId = localPeerId;
    }

    public static MessageListAdapter getInstance(
            final LayoutInflater inflater,
            final MessageListAdapterCallback callback,
            final long localPeerId)
    {
        if (inflater == null) return null;

        UserIdChecker userIdChecker =
                UserIdCheckerGenerator.generateUserIdChecker();

        if (userIdChecker == null) return null;
        if (!userIdChecker.isValid(localPeerId)) return null;

        return new MessageListAdapter(inflater, callback, localPeerId);
    }

    public boolean setCallback(
            final MessageListAdapterCallback callback)
    {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
    }

    public boolean setMessagesList(final List<MessageEntity> messages) {
        if (messages == null) return false;

        m_messages = messages;

        notifyDataSetChanged();

        return true;
    }

    public boolean addNewMessage(final MessageEntity message) {
        if (message == null) return false;

        m_messages.add(message);

        notifyItemInserted(m_messages.size() - 1);

        return true;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                    int viewType)
    {
        View view = m_inflater.inflate(R.layout.message_view_holder, parent, false);

        return new MessageListViewHolder(view, m_callback);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder,
                                 int position)
    {
        if (m_messages == null) return;

        MessageEntity message = m_messages.get(position);

        if (!holder.setMessageData(message, m_localPeerId)) {
            if (m_callback == null) return;

            m_callback.onErrorOccurred(
                    new Error("View Holder setting error has been occurred!", true)
            );

            return;
        }
    }

    @Override
    public int getItemCount() {
        return (m_messages == null ? 0 : m_messages.size());
    }
}
