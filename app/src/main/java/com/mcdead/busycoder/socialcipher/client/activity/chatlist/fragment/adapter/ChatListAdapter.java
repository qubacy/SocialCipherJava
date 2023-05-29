package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
    final private LayoutInflater m_inflater;

    private ChatListAdapterCallback m_chatListAdapterCallback = null;
    private ChatListItemCallback m_chatListItemCallback = null;

    private List<ChatEntity> m_dialogs = null;

    protected ChatListAdapter(
            final LayoutInflater layoutInflater,
            final ChatListAdapterCallback chatListAdapterCallback,
            final ChatListItemCallback chatListItemCallback)
    {
        m_inflater = layoutInflater;

        m_chatListAdapterCallback = chatListAdapterCallback;
        m_chatListItemCallback = chatListItemCallback;
    }

    public static ChatListAdapter getInstance(
            final LayoutInflater layoutInflater,
            final ChatListAdapterCallback chatListAdapterCallback,
            final ChatListItemCallback chatListItemCallback)
    {
        if (layoutInflater == null) return null;

        return new ChatListAdapter(layoutInflater, chatListAdapterCallback, chatListItemCallback);
    }

    public boolean setChatListCallback(
            final ChatListAdapterCallback chatListAdapterCallback)
    {
        if (chatListAdapterCallback == null || m_chatListAdapterCallback != null)
            return false;

        m_chatListAdapterCallback = chatListAdapterCallback;

        return true;
    }

    public boolean setChatListItemCallback(
            final ChatListItemCallback chatListItemCallback)
    {
        if (chatListItemCallback == null || m_chatListItemCallback != null)
            return false;

        m_chatListItemCallback = chatListItemCallback;

        return true;
    }

    public boolean setDialogsList(final List<ChatEntity> dialogs) {
        if (dialogs == null) return false;

        m_dialogs = dialogs;

        notifyDataSetChanged();

        return true;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType)
    {
        View view = m_inflater.inflate(R.layout.chat_view_holder, parent, false);

        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder,
                                 int position)
    {
        if (m_dialogs == null) return;

        ChatEntity dialog = m_dialogs.get(position);

        if (!holder.setDialogData(dialog)) {
            m_chatListAdapterCallback.onRecyclerViewAdapterErrorOccurred(
                    new Error("View Holder setting error has been occurred!", true)
            );

            return;
        }

        holder.setItemClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatEntity dialog = m_dialogs.get(holder.getAdapterPosition());

                if (dialog == null) {
                    m_chatListAdapterCallback.onRecyclerViewAdapterErrorOccurred(
                            new Error("Dialog cannot be found!", true)
                    );

                    return;
                }

                m_chatListItemCallback.onDialogItemClick(dialog.getDialogId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (m_dialogs == null ? 0 : m_dialogs.size());
    }
}
