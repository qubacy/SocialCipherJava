package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;

public class AttachmentListViewHolder extends RecyclerView.ViewHolder {
    final private View m_itemView;
    final private ImageView m_iconView;

    public AttachmentListViewHolder(@NonNull View itemView) {
        super(itemView);

        m_itemView = itemView;
        m_iconView = itemView.findViewById(R.id.chat_attachment_view_holder_icon);
    }

    public boolean setData(final AttachmentData attachmentData) {
        if (attachmentData == null) return false;

        switch (attachmentData.getType()) {
            case IMAGE: return setImageData(attachmentData);
            case DOC: return setDocData(attachmentData);
        }

        return false;
    }

    private boolean setImageData(final AttachmentData attachmentData) {
        m_iconView.setImageResource(R.drawable.ic_image_24);

        return true;
    }

    private boolean setDocData(final AttachmentData attachmentData) {
        m_iconView.setImageResource(R.drawable.ic_file_24);

        return true;
    }
}
