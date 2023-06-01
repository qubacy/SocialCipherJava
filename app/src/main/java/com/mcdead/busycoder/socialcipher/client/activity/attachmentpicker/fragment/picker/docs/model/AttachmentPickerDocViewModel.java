package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.model;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentPickerDocViewModel extends ViewModel {
    private List<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_docAttachmentDataList = null;

    public AttachmentPickerDocViewModel() {
        super();
    }

    public boolean setDocAttachmentDataList(
            final List<Pair<AttachmentData, ObjectWrapper<Boolean>>> docAttachmentDataList)
    {
        if (docAttachmentDataList == null || m_docAttachmentDataList != null)
            return false;

        m_docAttachmentDataList = docAttachmentDataList;

        return true;
    }

    public List<Pair<AttachmentData, ObjectWrapper<Boolean>>> getDocAttachmentDataList() {
        return m_docAttachmentDataList;
    }

    public boolean isInitialized() {
        return (m_docAttachmentDataList != null);
    }
}
