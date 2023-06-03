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
        if (m_callback == null) return;

        MessageEntity message = m_callback.getMessageByIndex(position);

        if (message == null) {
            m_callback.onErrorOccurred(
                    new Error("Message was null!", true)
            );

            return;
        }

        if (!holder.setMessageData(message, m_localPeerId)) {
            m_callback.onErrorOccurred(
                    new Error("View Holder setting error has been occurred!", true)
            );

            return;
        }
    }

    @Override
    public int getItemCount() {
        if (m_callback == null) return 0;

        return m_callback.getMessageListSize();
    }
}
