package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.AttachmentPickerDocFragment;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.DocPickerCallback;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.intent.DocPickerCallbackWrapper;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.intent.DocPickerContract;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.images.AttachmentPickerImageFragment;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.intent.AttachmentPickerActivityContract;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.type.AttachmentTypePickerFragment;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.type.AttachmentTypePickerFragmentCallback;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttachmentPickerActivity extends AppCompatActivity
    implements
        AttachmentTypePickerFragmentCallback,
        ActivityResultCallback<Map<String, Boolean>>,
        DocPickerCallback
{
    private AttachmentType m_attachmentType = null;

    private ActivityResultLauncher<Void> m_docPickerLauncher = null;

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

        Button confirmButton = findViewById(R.id.attachment_file_picker_confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmClicked();
            }
        });

        if (getSupportFragmentManager().
                findFragmentById(R.id.attachment_type_picker_wrapper) == null)
        {
            AttachmentTypePickerFragment attachmentTypePickerFragment =
                    AttachmentTypePickerFragment.getInstance(this);

            if (attachmentTypePickerFragment == null) {
                ErrorBroadcastReceiver.broadcastError(
                        new Error(
                                "Attachment Type Picking Fragment hasn't been initialized!",
                                true),
                        getApplicationContext()
                );
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.attachment_type_picker_wrapper, attachmentTypePickerFragment)
                    .commit();
        }

        m_docPickerLauncher =
                registerForActivityResult(
                    new DocPickerContract(),
                    new DocPickerCallbackWrapper(this));

        ActivityResultLauncher<String[]> permissionsRequestLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestMultiplePermissions(), this);

        permissionsRequestLauncher.launch(getPermissions());
    }

    private void setFilePickerFragment(final AttachmentType attachmentType) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        switch (attachmentType) {
            case IMAGE: {
                fragment = AttachmentPickerImageFragment.getInstance(this);

                break;
            }
            case DOC: {
                fragment = AttachmentPickerDocFragment.getInstance(this);

                openDocPicker();

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
                .replace(R.id.attachment_file_picker_wrapper, fragment)
                .commit();

        m_attachmentType = attachmentType;
    }

    private void openDocPicker() {
        m_docPickerLauncher.launch(null);
    }

    @Override
    public void onTypePicked(final AttachmentType type) {
        if (type == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error("Attachment Type was incorrect!", true),
                    getApplicationContext()
            );

            return;
        }

        setFilePickerFragment(type);
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
        if (m_attachmentType == null) return;

        Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.attachment_file_picker_wrapper);

        switch (m_attachmentType) {
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
        if (m_attachmentType != AttachmentType.DOC) return;

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
