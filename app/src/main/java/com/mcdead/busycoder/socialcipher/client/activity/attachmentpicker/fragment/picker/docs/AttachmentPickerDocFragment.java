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

    private AttachmentPickerDocAdapter m_docListAdapter = null;
    private RecyclerView m_docListView = null;

    private Context m_context = null;

    public AttachmentPickerDocFragment() {
        super();
    }

    public static AttachmentPickerDocFragment getInstance() {
        return new AttachmentPickerDocFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_attachmentPickerDocViewModel =
                new ViewModelProvider(getActivity()).get(AttachmentPickerDocViewModel.class);
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

        AttachmentPickerDocAdapter attachmentPickerDocAdapter =
                AttachmentPickerDocAdapter.getInstance(inflater, this);

        if (attachmentPickerDocAdapter == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Doc Attachment Picker Adapter hasn't been initialized!",
                            true),
                    m_context.getApplicationContext());

            return view;
        }

        m_docListAdapter = attachmentPickerDocAdapter;

        m_docListView.setAdapter(m_docListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (m_attachmentPickerDocViewModel.isInitialized()) {
            m_docListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        m_context = context;
    }

    @Override
    public void onDestroyView() {
        m_docListView.setAdapter(null);

        super.onDestroyView();
    }

    @Override
    public Pair<AttachmentData, ObjectWrapper<Boolean>> getDocAttachmentDataByIndex(int index) {
        Pair<AttachmentData, ObjectWrapper<Boolean>> docAttachmentData =
                m_attachmentPickerDocViewModel.getDocAttachmentDataByIndex(index);

        if (docAttachmentData == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Demanded Doc Attachment data was null!", true),
                    m_context.getApplicationContext());

            return null;
        }

        return docAttachmentData;
    }

    @Override
    public void onDocAttachmentDataChosenStateChanged(int index) {
        if (!m_attachmentPickerDocViewModel.changeDocAttachmentDataChosenStateByIndex(index)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Doc Attachment Data Chosen state changing went wrong!",
                            true),
                    m_context.getApplicationContext());

            return;
        }
    }

    @Override
    public int getDocAttachmentDataListSize() {
        return m_attachmentPickerDocViewModel.getDocDataListSize();
    }

    @Override
    public void onAttachmentPickerDocAdapterErrorOccurred(final Error error) {
        if (error == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Provided Error was null!", true),
                    m_context.getApplicationContext()
            );

            return;
        }

        ErrorBroadcastReceiver.broadcastError(
                error,
                m_context.getApplicationContext()
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
                    m_context.getApplicationContext());

            return;
        }

        m_docListAdapter.notifyDataSetChanged();
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
