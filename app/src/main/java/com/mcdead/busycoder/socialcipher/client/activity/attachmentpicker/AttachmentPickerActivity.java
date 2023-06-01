package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.AttachmentPickerDocFragment;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.DocPickerCallback;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.intent.DocPickerCallbackWrapper;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.intent.DocPickerContract;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.AttachmentPickerImageFragment;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.intent.AttachmentPickerActivityContract;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.model.AttachmentPickerViewModel;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttachmentPickerActivity extends AppCompatActivity
    implements
        ActivityResultCallback<Map<String, Boolean>>,
        DocPickerCallback
{
    private AttachmentPickerViewModel m_attachmentPickerViewModel = null;

    private ActivityResultLauncher<Void> m_docPickerLauncher = null;

    final private List<ImageButton> m_buttons;

    public AttachmentPickerActivity() {
        super();

        m_buttons = new ArrayList<>();
    }

    private static String[] getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
        }

        return new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attachment_picker);

        ImageButton imageButton =
                findViewById(R.id.attachment_type_picker_image_button);
        ImageButton fileButton =
                findViewById(R.id.attachment_type_picker_file_button);

        imageButton.setTag(AttachmentType.IMAGE);
        fileButton.setTag(AttachmentType.DOC);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTypePicked(AttachmentType.IMAGE);
            }
        });
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTypePicked(AttachmentType.DOC);
            }
        });

        m_buttons.add(imageButton);
        m_buttons.add(fileButton);

        Button confirmButton = findViewById(R.id.attachment_file_picker_confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmClicked();
            }
        });

        m_docPickerLauncher =
                registerForActivityResult(
                    new DocPickerContract(),
                    new DocPickerCallbackWrapper(this));

        ActivityResultLauncher<String[]> permissionsRequestLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestMultiplePermissions(), this);

        permissionsRequestLauncher.launch(getPermissions());

        m_attachmentPickerViewModel =
                new ViewModelProvider(this).get(AttachmentPickerViewModel.class);

        if (m_attachmentPickerViewModel.isInitialized()) {
            setFilePickerFragment(m_attachmentPickerViewModel.getAttachmentType(), true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setChosenButtonByAttachmentType(final AttachmentType attachmentType) {
        for (final ImageButton button : m_buttons) {
            if (button.getTag() == attachmentType)
                button.setBackgroundResource(R.drawable.attachment_picker_button_chosen_shape);
            else
                button.setBackgroundResource(R.drawable.attachment_picker_button_shape);
        }
    }

    private void setFilePickerFragment(
            final AttachmentType attachmentType,
            final boolean isCreating)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (attachmentType) {
            case IMAGE: {
                fragment = AttachmentPickerImageFragment.getInstance(this);

                break;
            }
            case DOC: {
                fragment = AttachmentPickerDocFragment.getInstance(this);

                if (!isCreating) openDocPicker();

                break;
            }
            default: return;
        }

        if (fragment == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Attachment Picking Fragment hasn't been initialized!",
                            true),
                    getApplicationContext()
            );

            return;
        }

        fragmentTransaction
                .replace(R.id.attachment_file_picker_wrapper, fragment);

        if (!getSupportFragmentManager().isDestroyed())
            fragmentTransaction.commit();

        setChosenButtonByAttachmentType(attachmentType);
    }

    private void openDocPicker() {
        m_docPickerLauncher.launch(null);
    }

    public void onTypePicked(final AttachmentType type) {
        if (!m_attachmentPickerViewModel.setAttachmentType(type)) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Attachment Type was incorrect!", true),
                    getApplicationContext()
            );

            return;
        }

        setFilePickerFragment(type, false);
    }

    @Override
    public void onActivityResult(
            final Map<String, Boolean> result)
    {
        if (result == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error(
                                    "Null permissions' requesting result has been provided!",
                                    true),
                            getApplicationContext()
                    );

            return;
        }

        for (final Map.Entry<String, Boolean> permission : result.entrySet()) {
            if (!permission.getValue()) {
                ErrorBroadcastReceiver
                        .broadcastError(
                                new Error(
                                        "Media permissions haven't been granted!",
                                        true),
                                getApplicationContext()
                        );

                return;
            }
        }
    }

    private void onConfirmClicked() {
        AttachmentType attachmentType = m_attachmentPickerViewModel.getAttachmentType();

        if (attachmentType == null) return;

        Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.attachment_file_picker_wrapper);

        switch (attachmentType) {
            case IMAGE: onImagesChoiceConfirmClicked(fragment); break;
            case DOC: onDocChoiceConfirmClicked(fragment); break;
        }
    }

    private void onImagesChoiceConfirmClicked(final Fragment fragment) {
        AttachmentPickerImageFragment imageFragment = (AttachmentPickerImageFragment) fragment;
        List<AttachmentData> chosenImageAttachmentDataList = imageFragment.getChosenImageDataList();

        setActivityAttachmentDataListResult(chosenImageAttachmentDataList);
    }

    private void onDocChoiceConfirmClicked(final Fragment fragment) {
        AttachmentPickerDocFragment docFragment = (AttachmentPickerDocFragment) fragment;
        List<AttachmentData> chosenDocAttachmentDataList = docFragment.getChosenDocDataList();

        setActivityAttachmentDataListResult(chosenDocAttachmentDataList);
    }

    private void setActivityAttachmentDataListResult(
            final List<AttachmentData> fileDataList)
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putSerializable(
                AttachmentPickerActivityContract.C_FILE_DATA_LIST_PROP_NAME,
                (Serializable) fileDataList);
        intent.putExtra(
                AttachmentPickerActivityContract.C_FILE_DATA_LIST_WRAPPER_PROP_NAME,
                bundle);

        setResult(
                AttachmentPickerActivityContract.C_RESULT_CODE_OK_VALUE,
                intent);
        finish();
    }

    @Override
    public void onDocsPicked(final List<Uri> docUriList) {
        if (docUriList == null) return;
        if (m_attachmentPickerViewModel.getAttachmentType() != AttachmentType.DOC) return;

        AttachmentPickerDocFragment fragment =
                (AttachmentPickerDocFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.attachment_file_picker_wrapper);

        if (fragment == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Docs Fragment doesn't exist!", true),
                            getApplicationContext()
                    );

            return;
        }

        List<AttachmentData> docAttachmentDataList = new ArrayList<>();

        for (final Uri docUri : docUriList) {
            DocumentFile docFile = DocumentFile.fromSingleUri(this, docUri);
            AttachmentData attachmentData =
                    new AttachmentData(
                            AttachmentType.DOC,
                            docFile.getType(),
                            docFile.getName(),
                            docUri);

            docAttachmentDataList.add(attachmentData);
        }

        fragment.setDocList(docAttachmentDataList);
    }
}
