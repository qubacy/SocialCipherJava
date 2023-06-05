package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

public class AttachmentPickerImageAdapter extends RecyclerView.Adapter<AttachmentPickerImageViewHolder>
    implements AttachmentPickerImageViewHolderCallback
{
    final private LayoutInflater m_inflater;
    final private AttachmentPickerImageAdapterCallback m_callback;

    protected AttachmentPickerImageAdapter(
            final LayoutInflater layoutInflater,
            final AttachmentPickerImageAdapterCallback callback)
    {
        super();

        setHasStableIds(true);

        m_inflater = layoutInflater;
        m_callback = callback;
    }

    public static AttachmentPickerImageAdapter getInstance(
            final LayoutInflater layoutInflater,
            final AttachmentPickerImageAdapterCallback callback)
    {
        if (layoutInflater == null || callback == null) return null;

        return new AttachmentPickerImageAdapter(layoutInflater, callback);
    }

    @NonNull
    @Override
    public AttachmentPickerImageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder =
                m_inflater.inflate(
                    R.layout.attachment_image_view_holder, parent, false);

        return new AttachmentPickerImageViewHolder(
                viewHolder, this, m_inflater.getContext());
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentPickerImageViewHolder holder,
            int position)
    {
        Pair<AttachmentData, ObjectWrapper<Boolean>> imageData =
                m_callback.getImageAttachmentDataByIndex(holder.getAdapterPosition());

        holder.setData(imageData.first.getUri(), imageData.second.getValue());
    }

    @Override
    public int getItemCount() {
        return m_callback.getImageAttachmentDataListSize();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean checkImageId(final int id) {
        if (id < 0 || m_callback.getImageAttachmentDataListSize() <= id) {
//            m_callback.onAttachmentPickerImageAdapterErrorOccurred(
//                    new Error("Wrong Image Id has been provided!", true)
//            );

            return false;
        }

        return true;
    }

    @Override
    public void onImageClicked(final int id) {
        if (!checkImageId(id)) return;

        m_callback.onImageAttachmentDataChosenStateChanged(id);
    }

    @Override
    public void onViewHolderErrorOccurred(final Error error) {
        m_callback.onAttachmentPickerImageAdapterErrorOccurred(error);
    }
}
