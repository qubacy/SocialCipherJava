package com.mcdead.busycoder.socialcipher.dialog;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class MessageListViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout m_itemWrapper = null;
    private TextView m_text = null;
    private TextView m_timestamp = null;
    //private RecyclerView m_attachments = null;

    public MessageListViewHolder(@NonNull View itemView) {
        super(itemView);

        m_itemWrapper = itemView.findViewById(R.id.message_view_holder_message_wrapper);
        m_text = itemView.findViewById(R.id.message_view_holder_message_text);
        m_timestamp = itemView.findViewById(R.id.message_view_holder_timestamp);
    }

    public boolean setMessageData(final MessageEntity message,
                                  final long localPeerId)
    {
        if (message == null) return false;

        int offsetInSec = TimeZone.getDefault().getRawOffset() / 1000;

        m_text.setText(message.getMessage());
        m_timestamp.setText(String.valueOf(
                LocalDateTime.ofEpochSecond(message.getTimestamp(), 0, ZoneOffset.ofTotalSeconds(offsetInSec)).toLocalTime()));

        ConstraintLayout.LayoutParams wrapperLayoutParams = (ConstraintLayout.LayoutParams) m_itemWrapper.getLayoutParams();
        ConstraintLayout.LayoutParams timestampLayoutParams = (ConstraintLayout.LayoutParams) m_timestamp.getLayoutParams();

        if (message.getFromPeerId() == localPeerId) {
            wrapperLayoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            wrapperLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;

            timestampLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            timestampLayoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;

        } else {
            wrapperLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            wrapperLayoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;

            timestampLayoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            timestampLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
        }

        m_itemWrapper.setLayoutParams(wrapperLayoutParams);

        return true;
    }
}
