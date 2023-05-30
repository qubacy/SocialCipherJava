package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter.AttachmentPickerImageAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter.AttachmentPickerImageAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher.ImageSearcher;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher.ImageSearcherCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

import java.util.List;

public class AttachmentPickerImageFragment extends Fragment
    implements
        AttachmentPickerImageAdapterCallback,
        ImageSearcherCallback
{
    public static final int C_IMAGE_GRID_CACHE_SIZE = 21;
    public static final int C_NUMB_OF_COLS = 3;

    private AttachmentPickerImageAdapter m_attachmentPickerImageAdapter = null;

    public AttachmentPickerImageFragment() {
        super();
    }

    protected AttachmentPickerImageFragment(
            final AttachmentPickerImageAdapter attachmentPickerImageAdapter)
    {
        m_attachmentPickerImageAdapter = attachmentPickerImageAdapter;
    }

    public static AttachmentPickerImageFragment getInstance(
            final AttachmentPickerImageAdapter attachmentPickerImageAdapter)
    {
        if (attachmentPickerImageAdapter == null) return null;

        return new AttachmentPickerImageFragment(attachmentPickerImageAdapter);
    }

    public static AttachmentPickerImageFragment getInstance(
            final Context context)
    {
        if (context == null) return null;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        AttachmentPickerImageAdapter attachmentPickerImageAdapter =
                AttachmentPickerImageAdapter.getInstance(layoutInflater, null);

        if (attachmentPickerImageAdapter == null) return null;

        return new AttachmentPickerImageFragment(attachmentPickerImageAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (m_attachmentPickerImageAdapter == null) {
            AttachmentPickerImageAdapter attachmentPickerImageAdapter =
                    AttachmentPickerImageAdapter.getInstance(getLayoutInflater(), this);

            if (attachmentPickerImageAdapter == null) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                                "Image Attachment Picker Adapter hasn't been initialized",
                                true),
                        getActivity().getApplicationContext());

                return;
            }

            m_attachmentPickerImageAdapter = attachmentPickerImageAdapter;

        } else
            m_attachmentPickerImageAdapter.setCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_attachment_image_picker, container, false);

        RecyclerView imageGridView = view.findViewById(R.id.attachment_image_picker_grid);

        imageGridView.setLayoutManager(new GridLayoutManager(getContext(), C_NUMB_OF_COLS));
        imageGridView.setHasFixedSize(true);
        imageGridView.setItemViewCacheSize(C_IMAGE_GRID_CACHE_SIZE);
        imageGridView.setDrawingCacheEnabled(true);
        imageGridView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        imageGridView.setAdapter(m_attachmentPickerImageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        new ImageSearcher(getContext(), this).execute();
    }

    @Override
    public void onAttachmentPickerImageAdapterErrorOccurred(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(
                        error, getActivity().getApplicationContext());
    }

    @Override
    public void onImageSearcherErrorOccurred(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(
                        error, getActivity().getApplicationContext());
    }

    @Override
    public void onImageSearcherImagesFound(final List<AttachmentData> imageAttachmentDataList) {
        if (!m_attachmentPickerImageAdapter.setImageList(imageAttachmentDataList)) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Image List setting problem has been occurred!", true),
                            getActivity().getApplicationContext());
        }
    }

    public List<AttachmentData> getChosenImageDataList() {
        return m_attachmentPickerImageAdapter.getChosenImages();
    }
}