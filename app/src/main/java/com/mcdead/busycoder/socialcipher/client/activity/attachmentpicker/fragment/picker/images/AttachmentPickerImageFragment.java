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

    private Context m_context = null;

    public AttachmentPickerImageFragment() {
        super();
    }

    public static AttachmentPickerImageFragment getInstance()
    {
        return new AttachmentPickerImageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_imagePickerViewModel =
                new ViewModelProvider(getActivity()).get(AttachmentPickerImageViewModel.class);
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

        AttachmentPickerImageAdapter attachmentPickerImageAdapter =
                AttachmentPickerImageAdapter.getInstance(getLayoutInflater(), this);

        if (attachmentPickerImageAdapter == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Image Attachment Picker Adapter hasn't been initialized",
                            true),
                    getActivity().getApplicationContext());

            return view;
        }

        m_attachmentPickerImageAdapter = attachmentPickerImageAdapter;

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
            new ImageSearcher(m_context, this).execute();
        else
            m_attachmentPickerImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart(); // todo: here is the model already reset. why?
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        m_context = context;
    }

    @Override
    public void onDestroyView() {
        m_imageGridView.setAdapter(null);

        super.onDestroyView();
    }

    @Override
    public Pair<AttachmentData, ObjectWrapper<Boolean>> getImageAttachmentDataByIndex(final int index) {
        Pair<AttachmentData, ObjectWrapper<Boolean>> imageAttachmentData =
                m_imagePickerViewModel.getImageDataByIndex(index);

        if (imageAttachmentData == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Demanded Image Attachment data was null!", true),
                    m_context.getApplicationContext());

            return null;
        }

        return m_imagePickerViewModel.getImageDataByIndex(index);
    }

    @Override
    public void onImageAttachmentDataChosenStateChanged(
            final int index)
    {
        if (!m_imagePickerViewModel.changeImageDataChosenStateByIndex(index)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Image Data Chosen state changing went wrong!",
                            true),
                    m_context.getApplicationContext());

            return;
        }
    }

    @Override
    public int getImageAttachmentDataListSize() {
        return m_imagePickerViewModel.getImageDataListSize();
    }

    @Override
    public void onAttachmentPickerImageAdapterErrorOccurred(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(
                        error, m_context.getApplicationContext());
    }

    @Override
    public void onImageSearcherErrorOccurred(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(
                        error, m_context.getApplicationContext());
    }

    @Override
    public void onImageSearcherImagesFound(final List<AttachmentData> imageAttachmentList) {
        if (imageAttachmentList == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Image List was null!", true),
                            m_context.getApplicationContext());

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
                            m_context.getApplicationContext());

            return;
        }

        m_attachmentPickerImageAdapter.notifyDataSetChanged();
    }

    public List<AttachmentData> getChosenImageDataList() {
        List<AttachmentData> chosenImageDataList = new ArrayList<>();

        for (final Pair<AttachmentData, ObjectWrapper<Boolean>> imageData :
                m_imagePickerViewModel.getImageDataList())
        {
            if (imageData.second.getValue())
                chosenImageDataList.add(imageData.first);
        }

        return chosenImageDataList;
    }
}
