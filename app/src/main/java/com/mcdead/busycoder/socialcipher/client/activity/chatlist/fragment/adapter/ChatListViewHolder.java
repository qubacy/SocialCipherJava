package com.mcdead.busycoder.socialcipher.client.activity.chatlist.fragment.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.utility.chat.ChatTitleExtractor;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.ChatEntity;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.utility.message.MessageTextGenerator;

public class ChatListViewHolder extends ViewHolder {
    final private View m_itemView;
    final private TextView m_peerNameTextView;
    final private TextView m_lastMessageTextView;

    public ChatListViewHolder(@NonNull View itemView) {
        super(itemView);

        m_itemView = itemView;
        m_peerNameTextView = itemView.findViewById(R.id.dialog_view_holder_peer_name);
        m_lastMessageTextView = itemView.findViewById(R.id.dialog_view_holder_last_message);
    }

    public boolean setDialogData(final ChatEntity chat) {
        if (chat == null) return false;

        String name = ChatTitleExtractor.getTitleByChat(chat);

        if (name == null) return false;

        m_peerNameTextView.setText(name);

        MessageEntity lastMessage = chat.getLastMessage();

        if (lastMessage != null)
            setChatLastMessage(lastMessage);

        return true;
    }

    public void setItemClickedListener(View.OnClickListener listener) {
        m_itemView.setOnClickListener(listener);
    }

    private void setChatLastMessage(final MessageEntity lastMessage) {
        String messageText = MessageTextGenerator.generateChatPreviewMessageText(lastMessage);

        m_lastMessageTextView.setText(messageText);
    }
}
