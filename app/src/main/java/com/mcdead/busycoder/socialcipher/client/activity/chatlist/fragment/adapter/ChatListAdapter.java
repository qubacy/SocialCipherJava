package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter;

import android.app.Activity;
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
    private LayoutInflater m_inflater = null;

    private ChatListAdapterCallback m_errorCallback = null;
    private ChatListItemCallback m_itemClickedCallback = null;

    private List<ChatEntity> m_dialogs = null;

    public ChatListAdapter(Activity activity,
                           ChatListAdapterCallback errorCallback,
                           ChatListItemCallback itemClickedCallback)
    {
        m_inflater = activity.getLayoutInflater();

        m_errorCallback = errorCallback;
        m_itemClickedCallback = itemClickedCallback;
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
        View view = m_inflater.inflate(R.layout.dialog_view_holder, parent, false);

        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder,
                                 int position)
    {
        if (m_dialogs == null) return;

        ChatEntity dialog = m_dialogs.get(position);

        if (!holder.setDialogData(dialog)) {
            m_errorCallback.onRecyclerViewAdapterErrorOccurred(
                    new Error("View Holder setting error has been occurred!", true)
            );

            return;
        }

        holder.setItemClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatEntity dialog = m_dialogs.get(holder.getAdapterPosition());

                if (dialog == null) {
                    m_errorCallback.onRecyclerViewAdapterErrorOccurred(
                            new Error("Dialog cannot be found!", true)
                    );

                    return;
                }

                m_itemClickedCallback.onDialogItemClick(dialog.getDialogId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (m_dialogs == null ? 0 : m_dialogs.size());
    }
}
