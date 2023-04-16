package com.mcdead.busycoder.socialcipher.attachmentpicker.images;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerImageAdapter extends RecyclerView.Adapter<AttachmentPickerImageViewHolder>
    implements AttachmentPickerImageViewHolderCallback
{
    private ContentResolver m_contentResolver = null;
    private LayoutInflater m_inflater = null;
    private AttachmentPickerImageAdapterCallback m_callback = null;

    private ArrayList<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_imageAttachmentDataList = null;

    public AttachmentPickerImageAdapter(
            @NonNull Context context,
            @NonNull AttachmentPickerImageAdapterCallback callback)
    {
        super();
        setHasStableIds(true);

        m_contentResolver = context.getContentResolver();
        m_inflater = LayoutInflater.from(context);
        m_callback = callback;

        m_imageAttachmentDataList = new ArrayList();
    }

    @NonNull
    @Override
    public AttachmentPickerImageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder = m_inflater.inflate(R.layout.attachment_image_view_holder, parent, false);

        return new AttachmentPickerImageViewHolder(viewHolder, this, m_contentResolver);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentPickerImageViewHolder holder,
            int position)
    {
        Pair<AttachmentData, ObjectWrapper<Boolean>> imageData = m_imageAttachmentDataList.get(position);

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
            m_imageAttachmentDataList.add(new Pair<>(imageAttachmentData, new ObjectWrapper<>(false)));
        }

        notifyDataSetChanged();

        return true;
    }

    public ArrayList<AttachmentData> getChosenImages() {
        ArrayList<AttachmentData> chosenImageAttachmentDataList = new ArrayList<>();

        for (final Pair<AttachmentData, ObjectWrapper<Boolean>> imageData : m_imageAttachmentDataList) {
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

        Pair<AttachmentData, ObjectWrapper<Boolean>> imageAttachmentData = m_imageAttachmentDataList.get(id);

        imageAttachmentData.second.setValue(!imageAttachmentData.second.getValue());
    }
}
