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

public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {
    final private LayoutInflater m_inflater;
    final private long m_localPeerId;
    final private MessageListAdapterCallback m_callback;

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
        if (inflater == null || callback == null) return null;

        UserIdChecker userIdChecker =
                UserIdCheckerGenerator.generateUserIdChecker();

        if (userIdChecker == null) return null;
        if (!userIdChecker.isValid(localPeerId)) return null;

        return new MessageListAdapter(inflater, callback, localPeerId);
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
        return m_callback.getMessageListSize();
    }
}
