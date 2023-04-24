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
    private View m_itemView = null;
    private TextView m_peerNameTextView = null;
    private TextView m_lastMessageTextView = null;

    public ChatListViewHolder(@NonNull View itemView) {
        super(itemView);

        m_itemView = itemView;
        m_peerNameTextView = itemView.findViewById(R.id.dialog_view_holder_peer_name);
        m_lastMessageTextView = itemView.findViewById(R.id.dialog_view_holder_last_message);
    }

    public boolean setDialogData(final ChatEntity dialog)
    {
        if (dialog == null) return false;

        String name = ChatTitleExtractor.getTitleByDialog(dialog);

        if (name == null) return false;

        m_peerNameTextView.setText(name);

        MessageEntity lastMessage = dialog.getLastMessage();

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
