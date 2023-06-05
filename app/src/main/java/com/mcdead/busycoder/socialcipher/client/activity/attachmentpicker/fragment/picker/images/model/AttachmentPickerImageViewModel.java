package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.model;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentPickerImageViewModel extends ViewModel {
    private List<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_imageAttachmentDataList = null;

    public AttachmentPickerImageViewModel() {
        super();
    }

    public boolean setImageDataList(
            final List<Pair<AttachmentData, ObjectWrapper<Boolean>>> imageAttachmentDataList)
    {
        if (imageAttachmentDataList == null) return false;

        m_imageAttachmentDataList = imageAttachmentDataList;

        return true;
    }

    public Pair<AttachmentData, ObjectWrapper<Boolean>> getImageDataByIndex(final int index) {
        if (m_imageAttachmentDataList == null) return null;
        if (index < 0 || index >= m_imageAttachmentDataList.size())
            return null;

        return m_imageAttachmentDataList.get(index);
    }

    public boolean changeImageDataChosenStateByIndex(final int index) {
        if (m_imageAttachmentDataList == null) return false;
        if (index < 0 || index >= m_imageAttachmentDataList.size())
            return false;

        Pair<AttachmentData, ObjectWrapper<Boolean>> imageData = m_imageAttachmentDataList.get(index);

        imageData.second.setValue(!imageData.second.getValue());

        return true;
    }

    public List<Pair<AttachmentData, ObjectWrapper<Boolean>>> getImageDataList() {
        return m_imageAttachmentDataList;
    }

    public int getImageDataListSize() {
        if (m_imageAttachmentDataList == null) return 0;

        return m_imageAttachmentDataList.size();
    }

    public boolean isInitialized() {
        return (m_imageAttachmentDataList != null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
