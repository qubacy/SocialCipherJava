package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityImage;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

public class AttachmentShowerFragment extends Fragment {
    public static final String C_TAG = "showerFragment";

    private AttachmentEntityBase m_attachment = null;

    public AttachmentShowerFragment(final AttachmentEntityBase attachment) {
        super();

        m_attachment = attachment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        FrameLayout view = (FrameLayout) inflater.inflate(
                R.layout.fragment_attachment_shower,
                container,
                false);

        View contentView = generateViewForAttachment();

        if (contentView == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error(
                                    "Attachment Content View hasn't been created!",
                                    true),
                            getActivity().getApplicationContext()
                    );

            return view;
        }

        view.addView(contentView);

        return view;
    }

    private View generateViewForAttachment() {
        AttachmentType attachmentType = m_attachment.getType();

        switch (attachmentType) {
            case IMAGE: return generateViewForAttachmentImage();
            case VIDEO:
            case AUDIO: break;
        }

        return null;
    }

    private View generateViewForAttachmentImage() {
        if (!(m_attachment instanceof AttachmentEntityImage))
            return null;

        AttachmentEntityImage imageAttachment = (AttachmentEntityImage) m_attachment;
        ImageView imageView = new ImageView(getActivity());

        imageView.setImageURI(Uri.parse(imageAttachment.getURI().toString()));

        return imageView;
    }
}
