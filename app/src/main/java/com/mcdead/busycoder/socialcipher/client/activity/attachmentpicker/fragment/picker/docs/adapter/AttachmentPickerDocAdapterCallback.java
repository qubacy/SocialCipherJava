package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

public interface AttachmentPickerDocAdapterCallback {
    public Pair<AttachmentData, ObjectWrapper<Boolean>> getDocAttachmentDataByIndex(final int index);
    public void onDocAttachmentDataChosenStateChanged(final int index);
    public int getDocAttachmentDataListSize();
    public void onAttachmentPickerDocAdapterErrorOccurred(final Error error);
}
