package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.images.adapter;

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

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerImageAdapter extends RecyclerView.Adapter<AttachmentPickerImageViewHolder>
    implements AttachmentPickerImageViewHolderCallback
{
    final private LayoutInflater m_inflater;
    private AttachmentPickerImageAdapterCallback m_callback = null;

    private ArrayList<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_imageAttachmentDataList = null;

    protected AttachmentPickerImageAdapter(
            final LayoutInflater layoutInflater,
            final AttachmentPickerImageAdapterCallback callback)
    {
        super();

        setHasStableIds(true);

        m_inflater = layoutInflater;
        m_callback = callback;

        m_imageAttachmentDataList = new ArrayList();
    }

    public static AttachmentPickerImageAdapter getInstance(
            final LayoutInflater layoutInflater,
            final AttachmentPickerImageAdapterCallback callback)
    {
        if (layoutInflater == null) return null;

        return new AttachmentPickerImageAdapter(layoutInflater, callback);
    }

    public boolean setCallback(
            final AttachmentPickerImageAdapterCallback callback)
    {
        if (callback == null || m_callback != null)
            return false;

        m_callback = callback;

        return true;
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
                m_imageAttachmentDataList.get(position);

        holder.setData(imageData.first.getUri(), imageData.second.getValue());
    }

    @Override
    public int getItemCount() {
        return m_imageAttachmentDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean setImageList(
            final List<AttachmentData> imageAttachmentDataList)
    {
        if (imageAttachmentDataList == null) return false;

        for (final AttachmentData imageAttachmentData : imageAttachmentDataList) {
            m_imageAttachmentDataList.add(
                    new Pair<>(imageAttachmentData, new ObjectWrapper<>(false)));
        }

        notifyDataSetChanged();

        return true;
    }

    public ArrayList<AttachmentData> getChosenImages() {
        ArrayList<AttachmentData> chosenImageAttachmentDataList = new ArrayList<>();

        for (final Pair<AttachmentData, ObjectWrapper<Boolean>> imageData :
                m_imageAttachmentDataList)
        {
            if (!imageData.second.getValue()) continue;

            chosenImageAttachmentDataList.add(imageData.first);
        }

        return chosenImageAttachmentDataList;
    }

    private boolean checkImageId(final int id) {
        if (id < 0 || m_imageAttachmentDataList.size() <= id) {
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

        Pair<AttachmentData, ObjectWrapper<Boolean>> imageAttachmentData =
                m_imageAttachmentDataList.get(id);

        imageAttachmentData.second.setValue(!imageAttachmentData.second.getValue());
    }

    @Override
    public void onViewHolderErrorOccurred(final Error error) {
        if (m_callback == null) return;

        m_callback.onAttachmentPickerImageAdapterErrorOccurred(error);
    }
}
