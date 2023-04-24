package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.chooser.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;

public class AttachmentListViewHolder extends RecyclerView.ViewHolder {
    private View m_itemView = null;
    private TextView m_name = null;

    public AttachmentListViewHolder(
            @NonNull View itemView)
    {
        super(itemView);

        m_itemView = itemView;
        m_name = itemView.findViewById(R.id.attachment_view_holder_name);
    }

    public boolean setData(final AttachmentEntityBase attachment) {
        if (attachment == null) return false;

        m_name.setText(attachment.getId());

        return true;
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        m_itemView.setOnClickListener(clickListener);
    }
}
