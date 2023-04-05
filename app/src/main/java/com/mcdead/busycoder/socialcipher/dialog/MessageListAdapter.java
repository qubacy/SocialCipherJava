package com.mcdead.busycoder.socialcipher.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.RecyclerViewAdapterErrorCallback;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.error.Error;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {
    private LayoutInflater m_inflater = null;
    private RecyclerViewAdapterErrorCallback m_errorCallback = null;
    private long m_localPeerId = 0;

    private List<MessageEntity> m_messages = null;

    public MessageListAdapter(Activity activity,
                              RecyclerViewAdapterErrorCallback errorCallback,
                              final long localPeerId)
    {
        m_inflater = activity.getLayoutInflater();
        m_errorCallback = errorCallback;
        m_localPeerId = localPeerId;
    }

    public boolean setMessagesList(List<MessageEntity> messages) {
        if (messages == null) return false;

        m_messages = messages;

        return true;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                    int viewType)
    {
        View view = m_inflater.inflate(R.layout.message_view_holder, parent, false);

        return new MessageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder,
                                 int position)
    {
        if (m_messages == null) return;

        MessageEntity message = m_messages.get(position);

        if (!holder.setMessageData(message, m_localPeerId)) {
            m_errorCallback.onRecyclerViewAdapterErrorOccurred(
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
