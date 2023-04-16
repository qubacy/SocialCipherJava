package com.mcdead.busycoder.socialcipher.attachmentpicker.images;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerImageFragment extends Fragment
    implements
        AttachmentPickerImageAdapterCallback,
        ImageSearcherCallback
{
    public static final int C_IMAGE_GRID_CACHE_SIZE = 21;
    public static final int C_NUMB_OF_COLS = 3;

    private AttachmentPickerImageAdapter m_imageGridAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        m_imageGridAdapter = new AttachmentPickerImageAdapter(getContext(), this);

        imageGridView.setLayoutManager(new GridLayoutManager(getContext(), C_NUMB_OF_COLS));
        imageGridView.setHasFixedSize(true);
        imageGridView.setItemViewCacheSize(C_IMAGE_GRID_CACHE_SIZE);
        imageGridView.setDrawingCacheEnabled(true);
        imageGridView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        imageGridView.setAdapter(m_imageGridAdapter);

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
        if (!m_imageGridAdapter.setImageList(imageAttachmentDataList)) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Image List setting problem has been occurred!", true),
                            getActivity().getApplicationContext());
        }
    }

    public List<AttachmentData> getChosenImageDataList() {
        return m_imageGridAdapter.getChosenImages();
    }
}
