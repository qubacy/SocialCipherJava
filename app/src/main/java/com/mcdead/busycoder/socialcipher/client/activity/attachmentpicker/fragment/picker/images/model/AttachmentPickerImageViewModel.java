package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.model;

import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentPickerImageViewModel extends ViewModel {
    private List<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_imageAttachmentDataList = null;

    public AttachmentPickerImageViewModel() {

    }

    public boolean setImageDataList(
            final List<Pair<AttachmentData, ObjectWrapper<Boolean>>> imageAttachmentDataList)
    {
        if (imageAttachmentDataList == null || m_imageAttachmentDataList != null)
            return false;

        m_imageAttachmentDataList = imageAttachmentDataList;

        return true;
    }

    public List<Pair<AttachmentData, ObjectWrapper<Boolean>>> getImageDataList() {
        return m_imageAttachmentDataList;
    }

    public boolean isInitialized() {
        return (m_imageAttachmentDataList != null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
