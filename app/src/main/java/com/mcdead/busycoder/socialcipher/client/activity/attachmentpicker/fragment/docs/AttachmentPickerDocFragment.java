package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs;

import android.content.Context;
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
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.adapter.AttachmentPickerDocAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.adapter.AttachmentPickerDocAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

import java.util.List;

public class AttachmentPickerDocFragment extends Fragment
    implements
        AttachmentPickerDocAdapterCallback
{
    final private AttachmentPickerDocAdapter m_docListAdapter;

    protected AttachmentPickerDocFragment(
            final AttachmentPickerDocAdapter attachmentPickerDocAdapter)
    {
        m_docListAdapter = attachmentPickerDocAdapter;
    }

    public static AttachmentPickerDocFragment getInstance(
            final AttachmentPickerDocAdapter attachmentPickerDocAdapter)
    {
        if (attachmentPickerDocAdapter == null) return null;

        return new AttachmentPickerDocFragment(attachmentPickerDocAdapter);
    }

    public static AttachmentPickerDocFragment getInstance(
            final Context context)
    {
        if (context == null) return null;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        AttachmentPickerDocAdapter attachmentPickerDocAdapter =
                AttachmentPickerDocAdapter.getInstance(layoutInflater, null);

        if (attachmentPickerDocAdapter == null) return null;

        return new AttachmentPickerDocFragment(attachmentPickerDocAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_docListAdapter.setCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view =
                inflater.inflate(
                    R.layout.fragment_attachment_doc_picker, container, false);

        RecyclerView docListView = view.findViewById(R.id.attachment_doc_picker_list);

        docListView.setLayoutManager(
                new LinearLayoutManager(
                        getContext(), LinearLayoutManager.VERTICAL, false));
        docListView.setAdapter(m_docListAdapter);

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
    public void onAttachmentPickerDocAdapterErrorOccurred(final Error error) {
        if (error == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Provided Error was null!", true),
                    getContext().getApplicationContext()
            );

            return;
        }

        ErrorBroadcastReceiver.broadcastError(
                error,
                getContext().getApplicationContext()
        );
    }

    public void setDocList(final List<AttachmentData> docAttachmentDataList) {
        if (docAttachmentDataList == null) return;

        m_docListAdapter.setDocList(docAttachmentDataList);
    }

    public List<AttachmentData> getChosenDocDataList() {
        return m_docListAdapter.getChosenDocList();
    }
}
