package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter.AttachmentPickerDocAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter.AttachmentPickerDocAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.model.AttachmentPickerDocViewModel;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerDocFragment extends Fragment
    implements
        AttachmentPickerDocAdapterCallback
{
    private AttachmentPickerDocViewModel m_attachmentPickerDocViewModel = null;

    private AttachmentPickerDocAdapter m_docListAdapter;
    private RecyclerView m_docListView = null;

    public AttachmentPickerDocFragment() {
        super();
    }

    protected AttachmentPickerDocFragment(
            final AttachmentPickerDocAdapter attachmentPickerDocAdapter)
    {
        super();

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

        m_attachmentPickerDocViewModel =
                new ViewModelProvider(getActivity()).get(AttachmentPickerDocViewModel.class);

        if (m_attachmentPickerDocViewModel.isInitialized()) {
            m_docListAdapter = m_attachmentPickerDocViewModel.getDocListAdapter();
            m_docListAdapter.setDocList(m_attachmentPickerDocViewModel.getDocAttachmentDataList());

        } else {
            if (m_docListAdapter == null) {
                AttachmentPickerDocAdapter attachmentPickerDocAdapter =
                        AttachmentPickerDocAdapter.getInstance(getLayoutInflater(), this);

                if (attachmentPickerDocAdapter == null) {
                    ErrorBroadcastReceiver.broadcastError(
                            new Error(
                                    "Doc Attachment Picker Adapter hasn't been initialized!",
                                    true),
                            getActivity().getApplicationContext());

                    return;
                }

                m_docListAdapter = attachmentPickerDocAdapter;

            } else
                m_docListAdapter.setCallback(this);

            m_attachmentPickerDocViewModel.setDocListAdapter(m_docListAdapter);
        }
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

        m_docListView = view.findViewById(R.id.attachment_doc_picker_list);

        m_docListView.setLayoutManager(
                new LinearLayoutManager(
                        getContext(), LinearLayoutManager.VERTICAL, false));
        m_docListView.setAdapter(m_docListAdapter);

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
    public void onDestroyView() {
        m_docListView.setAdapter(null);

        super.onDestroyView();
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

    public void setDocList(final List<AttachmentData> docAttachmentList) {
        if (docAttachmentList == null) return;

        List<Pair<AttachmentData, ObjectWrapper<Boolean>>> docAttachmentDataList =
                new ArrayList<>();

        for (final AttachmentData attachmentData : docAttachmentList) {
            docAttachmentDataList.add(new Pair<>(attachmentData, new ObjectWrapper<>(false)));
        }

        if (!m_attachmentPickerDocViewModel.setDocAttachmentDataList(docAttachmentDataList)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Attachment Doc. Data list setting has been failed!",
                            true),
                    getActivity().getApplicationContext());

            return;
        }

        m_docListAdapter.setDocList(m_attachmentPickerDocViewModel.getDocAttachmentDataList());
    }

    public List<AttachmentData> getChosenDocDataList() {
        List<Pair<AttachmentData, ObjectWrapper<Boolean>>> docAttachmentDataList =
                m_attachmentPickerDocViewModel.getDocAttachmentDataList();
        List<AttachmentData> chosenAttachmentDataList = new ArrayList<>();

        for (final Pair<AttachmentData, ObjectWrapper<Boolean>> docAttachmentData :
                docAttachmentDataList)
        {
            if (docAttachmentData.second.getValue())
                chosenAttachmentDataList.add(docAttachmentData.first);
        }

        return chosenAttachmentDataList;
    }
}
