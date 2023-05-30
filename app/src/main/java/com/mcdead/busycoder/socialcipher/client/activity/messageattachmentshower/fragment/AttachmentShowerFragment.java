package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.fragment;

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
import androidx.lifecycle.ViewModelProvider;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.fragment.model.AttachmentShowerViewModel;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityImage;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

public class AttachmentShowerFragment extends Fragment {
    public static final String C_TAG = "showerFragment";

    private static final String C_ATTACHMENT_ARG_NAME = "attachment";

    private AttachmentShowerViewModel m_attachmentShowerViewModel = null;

    public AttachmentShowerFragment() {
        super();
    }

    protected AttachmentShowerFragment(
            final Bundle args)
    {
        super();

        setArguments(args);
    }

    public static AttachmentShowerFragment getInstance(
            final AttachmentEntityBase attachment)
    {
        if (attachment == null) return null;

        Bundle args = new Bundle();

        args.putSerializable(C_ATTACHMENT_ARG_NAME, attachment);

        return new AttachmentShowerFragment(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_attachmentShowerViewModel =
                new ViewModelProvider(this).get(AttachmentShowerViewModel.class);

        if (!m_attachmentShowerViewModel.isInitialized()) {
            Bundle args = getArguments();

            if (args == null) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                                "Args were null!",
                                true),
                        getActivity().getApplicationContext());

                return;
            }

            AttachmentEntityBase attachment =
                    (AttachmentEntityBase) args.getSerializable(C_ATTACHMENT_ARG_NAME);

            if (!m_attachmentShowerViewModel.setAttachment(attachment)) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                                "Attachment to show hasn't been set!",
                                true),
                        getActivity().getApplicationContext());

                return;
            }
        }
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
        AttachmentType attachmentType = m_attachmentShowerViewModel.getAttachment().getType();

        switch (attachmentType) {
            case IMAGE: return generateViewForAttachmentImage();
            case VIDEO:
            case AUDIO: break;
        }

        return null;
    }

    private View generateViewForAttachmentImage() {
        AttachmentEntityBase attachment = m_attachmentShowerViewModel.getAttachment();

        if (!(attachment instanceof AttachmentEntityImage))
            return null;

        AttachmentEntityImage imageAttachment = (AttachmentEntityImage) attachment;
        ImageView imageView = new ImageView(getActivity());

        imageView.setImageURI(
                Uri.parse(imageAttachment.getURIBySize(AttachmentSize.STANDARD).toString()));

        return imageView;
    }
}
