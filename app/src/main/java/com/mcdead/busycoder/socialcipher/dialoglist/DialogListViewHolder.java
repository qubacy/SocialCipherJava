package com.mcdead.busycoder.socialcipher.dialoglist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.entity.DialogTitleExtractor;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

public class DialogListViewHolder extends ViewHolder {
    private View m_itemView = null;
    private TextView m_peerNameTextView = null;
    private TextView m_lastMessageTextView = null;

    public DialogListViewHolder(@NonNull View itemView) {
        super(itemView);

        m_itemView = itemView;
        m_peerNameTextView = itemView.findViewById(R.id.dialog_view_holder_peer_name);
        m_lastMessageTextView = itemView.findViewById(R.id.dialog_view_holder_last_message);
    }

    public boolean setDialogData(final DialogEntity dialog)
    {
        if (dialog == null) return false;

        String name = DialogTitleExtractor.getTitleByDialog(dialog);

        if (name != null)
            m_peerNameTextView.setText(name);

        MessageEntity lastMessage = dialog.getLastMessage();

        if (lastMessage != null)
            m_lastMessageTextView.setText(lastMessage.getMessage());

        return true;
    }

    public void setItemClickedListener(View.OnClickListener listener) {
        m_itemView.setOnClickListener(listener);
    }
}
