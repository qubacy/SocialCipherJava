package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter.AttachmentPickerImageAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter.AttachmentPickerImageAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.model.AttachmentPickerImageViewModel;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher.ImageSearcher;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.searcher.ImageSearcherCallback;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerImageFragment extends Fragment
    implements
        AttachmentPickerImageAdapterCallback,
        ImageSearcherCallback
{
    public static final int C_IMAGE_GRID_CACHE_SIZE = 21;
    public static final int C_NUMB_OF_COLS = 3;

    private AttachmentPickerImageViewModel m_imagePickerViewModel = null;

    private AttachmentPickerImageAdapter m_attachmentPickerImageAdapter = null;

    private RecyclerView m_imageGridView = null;

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

        m_imagePickerViewModel =
                new ViewModelProvider(getActivity()).get(AttachmentPickerImageViewModel.class);

        if (!m_imagePickerViewModel.isInitialized()) {
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

            m_imagePickerViewModel.setAttachmentPickerImageAdapter(m_attachmentPickerImageAdapter);

        } else {
            m_attachmentPickerImageAdapter =
                    m_imagePickerViewModel.getAttachmentPickerImageAdapter();
            m_attachmentPickerImageAdapter.setImageDataList(
                    m_imagePickerViewModel.getImageDataList());
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view =
                inflater.inflate(
                        R.layout.fragment_attachment_image_picker, container, false);

        m_imageGridView = view.findViewById(R.id.attachment_image_picker_grid);

        m_imageGridView.setLayoutManager(new GridLayoutManager(getContext(), C_NUMB_OF_COLS));
        m_imageGridView.setHasFixedSize(true);
        m_imageGridView.setItemViewCacheSize(C_IMAGE_GRID_CACHE_SIZE);
        m_imageGridView.setDrawingCacheEnabled(true);
        m_imageGridView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        m_imageGridView.setAdapter(m_attachmentPickerImageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (!m_imagePickerViewModel.isInitialized())
            new ImageSearcher(getContext(), this).execute();
    }

    @Override
    public void onStart() {
        super.onStart(); // todo: here is the model already reset. why?
    }

    @Override
    public void onDestroyView() {
        m_imageGridView.setAdapter(null);

        super.onDestroyView();
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
    public void onImageSearcherImagesFound(final List<AttachmentData> imageAttachmentList) {
        if (imageAttachmentList == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Image List was null!", true),
                            getActivity().getApplicationContext());

            return;
        }

        ArrayList<Pair<AttachmentData, ObjectWrapper<Boolean>>> imageAttachmentDataList =
                new ArrayList<>();

        for (final AttachmentData imageAttachmentData : imageAttachmentList) {
            imageAttachmentDataList.add(
                    new Pair<>(imageAttachmentData, new ObjectWrapper<>(false)));
        }

        if (!m_imagePickerViewModel.setImageDataList(imageAttachmentDataList)) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Image Data List setting has been failed!", true),
                            getActivity().getApplicationContext());

            return;
        }

        m_attachmentPickerImageAdapter.setImageDataList(m_imagePickerViewModel.getImageDataList());
    }

    public List<AttachmentData> getChosenImageDataList() {
        return m_attachmentPickerImageAdapter.getChosenImages();
    }
}
