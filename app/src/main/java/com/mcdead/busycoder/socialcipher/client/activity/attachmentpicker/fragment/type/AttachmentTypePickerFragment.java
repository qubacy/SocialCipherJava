package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.type;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;

import java.util.ArrayList;
import java.util.List;

public class AttachmentTypePickerFragment extends Fragment {
    private AttachmentTypePickerFragmentCallback m_callback = null;

    private List<AppCompatImageButton> m_buttons = null;

    public AttachmentTypePickerFragment(
            final AttachmentTypePickerFragmentCallback callback)
    {
        m_callback = callback;

        m_buttons = new ArrayList<>();
    }

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
        View view = inflater.inflate(R.layout.fragment_attachment_type_picker, container, false);

        AppCompatImageButton imageButton = view.findViewById(R.id.attachment_type_picker_image_button);
        AppCompatImageButton fileButton = view.findViewById(R.id.attachment_type_picker_file_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_callback.onTypePicked(AttachmentType.IMAGE);

                setChosenButton(view.getId());
            }
        });
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_callback.onTypePicked(AttachmentType.DOC);

                setChosenButton(view.getId());
            }
        });

        m_buttons.add(imageButton);
        m_buttons.add(fileButton);

        return view;
    }

    private void setChosenButton(int buttonResourceId) {
        Drawable chosenButtonBackground
                = ResourcesCompat.getDrawable(getResources(),
                    R.drawable.attachment_picker_button_chosen_shape,
                    null);
        Drawable defaultButtonBackground
                = ResourcesCompat.getDrawable(getResources(),
                R.drawable.attachment_picker_button_shape,
                null);

        for (final AppCompatImageButton button : m_buttons) {
            if (button.getId() == buttonResourceId)
                button.setBackground(chosenButtonBackground);
            else
                button.setBackground(defaultButtonBackground);
        }
    }
}
