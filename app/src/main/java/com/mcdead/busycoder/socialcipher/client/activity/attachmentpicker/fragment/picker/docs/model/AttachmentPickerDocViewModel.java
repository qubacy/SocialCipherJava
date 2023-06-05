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
        if (docAttachmentDataList == null) return false;

        m_docAttachmentDataList = docAttachmentDataList;

        return true;
    }

    public List<Pair<AttachmentData, ObjectWrapper<Boolean>>> getDocAttachmentDataList() {
        return m_docAttachmentDataList;
    }

    public Pair<AttachmentData, ObjectWrapper<Boolean>> getDocAttachmentDataByIndex(
            final int index)
    {
        if (m_docAttachmentDataList == null) return null;
        if (index < 0 || index >= m_docAttachmentDataList.size()) return null;

        return m_docAttachmentDataList.get(index);
    }

    public boolean changeDocAttachmentDataChosenStateByIndex(final int index) {
        if (m_docAttachmentDataList == null) return false;
        if (index < 0 || index >= m_docAttachmentDataList.size()) return false;

        Pair<AttachmentData, ObjectWrapper<Boolean>> docAttachmentData =
                m_docAttachmentDataList.get(index);

        docAttachmentData.second.setValue(!docAttachmentData.second.getValue());

        return true;
    }

    public int getDocDataListSize() {
        if (m_docAttachmentDataList == null) return 0;

        return m_docAttachmentDataList.size();
    }

    public boolean isInitialized() {
        return (m_docAttachmentDataList != null);
    }
}
