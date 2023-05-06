package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityDoc;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityImage;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.MessageEntity;
import com.mcdead.busycoder.socialcipher.client.data.utility.message.MessageTextGenerator;
import com.mcdead.busycoder.socialcipher.utility.TimeUtility;

import java.net.URI;
import java.time.LocalTime;
import java.util.List;

public class MessageListViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout m_itemWrapper = null;
    private TextView m_text = null;
    private View m_attachmentPreview = null;
    private TextView m_timestamp = null;
    private ImageView m_cipherIndicator = null;
    //private RecyclerView m_attachments = null;

    private MessageListItemCallback m_callback = null;

    public MessageListViewHolder(
            @NonNull View itemView,
            MessageListItemCallback callback)
    {
        super(itemView);

        m_itemWrapper = itemView.findViewById(R.id.message_view_holder_message_wrapper);
        m_text = itemView.findViewById(R.id.message_view_holder_message_text);
        m_attachmentPreview = itemView.findViewById(R.id.message_view_holder_attachment_preview);
        m_timestamp = itemView.findViewById(R.id.message_view_holder_timestamp);
        m_cipherIndicator = itemView.findViewById(R.id.message_view_holder_cipher_indicator);

        m_callback = callback;
    }

    public boolean setMessageData(
            final MessageEntity message,
            final long localPeerId)
    {
        if (message == null) return false;

        LocalTime localTime =
                TimeUtility.getLocalTimeFromUTCMilliseconds(message.getTimestamp());

        m_text.setText(MessageTextGenerator.generateChatMessageText(message));
        m_timestamp.setText(String.valueOf(localTime));

        ConstraintLayout.LayoutParams wrapperLayoutParams =
                (ConstraintLayout.LayoutParams) m_itemWrapper.getLayoutParams();
        ConstraintLayout.LayoutParams timestampLayoutParams =
                (ConstraintLayout.LayoutParams) m_timestamp.getLayoutParams();
        ConstraintLayout.LayoutParams cipherIndicatorLayoutParams =
                (ConstraintLayout.LayoutParams) m_cipherIndicator.getLayoutParams();

        if (message.getFromPeerId() == localPeerId) {
            wrapperLayoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            wrapperLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;

            timestampLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            timestampLayoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;

            cipherIndicatorLayoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cipherIndicatorLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;

        } else {
            wrapperLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            wrapperLayoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;

            timestampLayoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            timestampLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;

            cipherIndicatorLayoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cipherIndicatorLayoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;
        }

        if (!setMessageAttachmentsData(message)) return false;

        m_itemWrapper.setLayoutParams(wrapperLayoutParams);
        m_timestamp.setLayoutParams(timestampLayoutParams);
        m_cipherIndicator.setLayoutParams(cipherIndicatorLayoutParams);

        if (message.isCiphered())
            m_cipherIndicator.setImageResource(R.drawable.ic_lock_green_24);
        else
            m_cipherIndicator.setImageResource(R.drawable.ic_lock_red_24);

        return true;
    }

    private boolean setMessageAttachmentsData(
            final MessageEntity message)
    {
        m_attachmentPreview.setVisibility(View.GONE);

        List<AttachmentEntityBase> attachments = message.getAttachments();

        if (attachments == null) return true;
        if (attachments.isEmpty()) return true;

        FrameLayout attachmentPreviewFrame =
                m_attachmentPreview.findViewById(R.id.attachment_preview_wrapper);
        Button attachmentsExpandButton =
                m_attachmentPreview.findViewById(R.id.attachments_list_expand_button);

        if (attachmentPreviewFrame == null
         || attachmentsExpandButton == null)
        {
            return true;
        }

        attachmentPreviewFrame.removeAllViews();

        AttachmentEntityBase attachmentToPreview = attachments.get(0);

        if (attachmentToPreview == null)
            return false;

        View attachmentPreview = null;

        switch (attachmentToPreview.getType()) {
            case DOC: attachmentPreview =
                    setMessageAttachmentDataDoc((AttachmentEntityDoc) attachmentToPreview); break;
            case IMAGE: attachmentPreview =
                    setMessageAttachmentDataImage((AttachmentEntityImage) attachmentToPreview); break;
        }

        if (attachmentPreview == null)
            return true;

        attachmentPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_callback.onAttachmentsShowClicked(message);
            }
        });

        attachmentPreviewFrame.addView(attachmentPreview);
        m_attachmentPreview.setVisibility(View.VISIBLE);

        if (attachments.size() > 1) {
            attachmentsExpandButton.setVisibility(View.VISIBLE);

            attachmentsExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_callback.onAttachmentsShowClicked(message);
                }
            });

        } else
            attachmentsExpandButton.setVisibility(View.GONE);

        return true;
    }

    private View setMessageAttachmentDataImage(
            final AttachmentEntityImage attachmentImage)
    {
        ImageView attachmentImageView = new ImageView(m_attachmentPreview.getContext());
        URI imageUri = attachmentImage.getURIBySize(AttachmentSize.SMALL);

        if (imageUri == null)
            imageUri = attachmentImage.getURIBySize(AttachmentSize.STANDARD);

        attachmentImageView.setImageURI(Uri.parse(imageUri.toString()));

        return attachmentImageView;
    }

    private View setMessageAttachmentDataDoc(
            final AttachmentEntityDoc attachmentDoc)
    {
        Drawable fileIcon = m_attachmentPreview.getContext().getDrawable(R.drawable.ic_file_24);

        if (fileIcon == null) return null;

        ImageView attachmentImageView = new ImageView(m_attachmentPreview.getContext());
//        Uri docUri = Uri.parse(attachmentDoc.getURI().toString());

        attachmentImageView.setImageDrawable(fileIcon);

//        // todo: think of this kind of handlers' competition:
//
//        attachmentImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                m_callback.onLinkedAttachmentClicked(docUri);
//            }
//        });

        return attachmentImageView;
    }
}
