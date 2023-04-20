package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser.adapter.AttachmentListAdapter;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser.adapter.AttachmentListAdapterCallback;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

import java.util.List;

public class AttachmentChooserFragment extends Fragment
    implements AttachmentListAdapterCallback
{
    public static final String C_TAG = "chooserFragment";

    private List<AttachmentEntityBase> m_attachmentList = null;
    private AttachmentChooserCallback m_callback = null;

    public AttachmentChooserFragment(
            final List<AttachmentEntityBase> attachmentList,
            final AttachmentChooserCallback callback)
    {
        super();

        m_attachmentList = attachmentList;
        m_callback = callback;
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
        View view = inflater.inflate(R.layout.fragment_attachment_chooser, container, false);

        RecyclerView attachmentListView = view.findViewById(R.id.attachment_chooser_list);
        AttachmentListAdapter attachmentListAdapter
                = new AttachmentListAdapter(m_attachmentList, getLayoutInflater(), this);

        attachmentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachmentListView.setAdapter(attachmentListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttachmentListError(final Error error) {
        if (error == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Error Data hasn't been provided", true),
                            getActivity().getApplicationContext());

            return;
        }

        ErrorBroadcastReceiver
                .broadcastError(error, getActivity().getApplicationContext());
    }

    @Override
    public void onAttachmentChosen(AttachmentEntityBase attachment) {
        if (attachment == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Attachment Data hasn't been provided", true),
                            getActivity().getApplicationContext());

            return;
        }

        m_callback.onAttachmentChosen(attachment);
    }
}
