package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

public interface AttachmentPickerImageAdapterCallback {
    public Pair<AttachmentData, ObjectWrapper<Boolean>> getImageAttachmentDataByIndex(final int index);
    public void onImageAttachmentDataChosenStateChanged(final int index);
    public int getImageAttachmentDataListSize();
    public void onAttachmentPickerImageAdapterErrorOccurred(final Error error);
}
