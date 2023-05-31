package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityDoc;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityImage;

public class AttachmentListViewHolder extends RecyclerView.ViewHolder {
    final private View m_itemView;
    final private ImageView m_iconView;

    public AttachmentListViewHolder(
            final View itemView,
            final ImageView iconView)
    {
        super(itemView);

        m_itemView = itemView;
        m_iconView = iconView;
    }

    public static AttachmentListViewHolder getInstance(
            final View itemView)
    {
        if (itemView == null) return null;

        ImageView iconView = itemView.findViewById(R.id.attachment_shower_view_holder_icon);

        if (iconView == null) return null;

        return new AttachmentListViewHolder(itemView, iconView);
    }

    public boolean setData(
            final AttachmentEntityBase attachmentDataToShow,
            final boolean isChosen)
    {
        if (attachmentDataToShow == null) return false;

        if (isChosen) setActiveState(true);

        switch (attachmentDataToShow.getType()) {
            case IMAGE: return setDataForImage((AttachmentEntityImage) attachmentDataToShow);
            case DOC: return setDataForDoc((AttachmentEntityDoc) attachmentDataToShow);
        }

        return false;
    }

    private boolean setDataForImage(final AttachmentEntityImage attachment) {
        m_iconView.setImageResource(R.drawable.ic_image_24);

        return true;
    }

    private boolean setDataForDoc(final AttachmentEntityDoc attachment) {
        m_iconView.setImageResource(R.drawable.ic_file_24);

        return true;
    }

    public void setOnItemClickListener(final View.OnClickListener clickListener) {
        m_itemView.setOnClickListener(clickListener);
    }

    public void setActiveState(final boolean isActive) {
        if (isActive)
            m_itemView.setBackgroundResource(R.drawable.attachment_shower_button_shape_pressed);
        else
            m_itemView.setBackgroundResource(R.drawable.attachment_shower_button_shape);
    }
}
