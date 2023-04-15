package com.mcdead.busycoder.socialcipher.attachmentpicker.images;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerImageAdapter extends RecyclerView.Adapter<AttachmentPickerImageViewHolder>
    implements AttachmentPickerImageViewHolderCallback
{
    private ContentResolver m_contentResolver = null;
    private LayoutInflater m_inflater = null;
    private AttachmentPickerImageAdapterCallback m_callback = null;

    private ArrayList<Pair<Uri, ObjectWrapper<Boolean>>> m_imageDataList = null;

    public AttachmentPickerImageAdapter(
            @NonNull Context context,
            @NonNull AttachmentPickerImageAdapterCallback callback)
    {
        super();
        setHasStableIds(true);

        m_contentResolver = context.getContentResolver();
        m_inflater = LayoutInflater.from(context);
        m_callback = callback;

        m_imageDataList = new ArrayList();
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
        Pair<Uri, ObjectWrapper<Boolean>> imageData = m_imageDataList.get(position);

        holder.setData(imageData.first, imageData.second.getValue());
    }

    @Override
    public int getItemCount() {
        return m_imageDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean setImageList(
            final List<Uri> imageUriList)
    {
        if (imageUriList == null) return false;

        for (final Uri imageUri : imageUriList) {
            m_imageDataList.add(new Pair<>(imageUri, new ObjectWrapper<>(false)));
        }

        notifyDataSetChanged();

        return true;
    }

    public ArrayList<Uri> getChosenImages() {
        ArrayList<Uri> chosenImageUriList = new ArrayList<>();

        for (final Pair<Uri, ObjectWrapper<Boolean>> imageData : m_imageDataList) {
            if (!imageData.second.getValue()) continue;

            chosenImageUriList.add(imageData.first);
        }

        return chosenImageUriList;
    }

    private boolean checkImageId(final int id) {
        if (id < 0 || m_imageDataList.size() <= id) {
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

        Pair<Uri, ObjectWrapper<Boolean>> imageData = m_imageDataList.get(id);

        imageData.second.setValue(!imageData.second.getValue());
    }
}
