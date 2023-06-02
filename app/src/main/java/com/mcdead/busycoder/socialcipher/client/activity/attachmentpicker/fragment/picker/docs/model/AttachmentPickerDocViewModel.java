package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.model;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter.AttachmentPickerDocAdapter;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentPickerDocViewModel extends ViewModel {
    private List<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_docAttachmentDataList = null;
    private AttachmentPickerDocAdapter m_docListAdapter = null;

    public AttachmentPickerDocViewModel() {
        super();
    }

    public boolean setDocAttachmentDataList(
            final List<Pair<AttachmentData, ObjectWrapper<Boolean>>> docAttachmentDataList)
    {
        if (docAttachmentDataList == null) return false;

        m_docAttachmentDataList = docAttachmentDataList;

        return true;
    }

    public boolean setDocListAdapter(
            final AttachmentPickerDocAdapter attachmentPickerDocAdapter)
    {
        if (attachmentPickerDocAdapter == null) return false;

        m_docListAdapter = attachmentPickerDocAdapter;

        return true;
    }

    public List<Pair<AttachmentData, ObjectWrapper<Boolean>>> getDocAttachmentDataList() {
        return m_docAttachmentDataList;
    }

    public AttachmentPickerDocAdapter getDocListAdapter() {
        return m_docListAdapter;
    }

    public boolean isInitialized() {
        return (m_docListAdapter != null);
    }
}
