package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.attachmentdoc.AttachmentDocUtility;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.attachmentdoc.LinkedFileOpenerAsync;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.attachmentdoc.LinkedFileOpenerCallback;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.fragment.AttachmentShowerFragment;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityDoc;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser.AttachmentChooserCallback;
import com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser.AttachmentChooserFragment;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentShowerActivity extends AppCompatActivity
    implements
        AttachmentChooserCallback,
        LinkedFileOpenerCallback
{
    public static final String C_ATTACHMENT_LIST_WRAPPER_PROP_NAME = "attachmentListWrapper";
    public static final String C_ATTACHMENT_LIST_PROP_NAME = "attachmentList";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attachment_shower);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(R.string.attachment_shower_action_bar_title);
        }

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            ObjectWrapper<List<AttachmentEntityBase>> attachmentListWrapper
                    = new ObjectWrapper<>();
            Error retrievingError
                    = retrieveAttachmentsFromIntent(getIntent(), attachmentListWrapper);

            if (retrievingError != null) {
                finishWithError(retrievingError);

                return;
            }

            showChooser(attachmentListWrapper.getValue());
        }
    }

    private Error retrieveAttachmentsFromIntent(
            final Intent intent,
            ObjectWrapper<List<AttachmentEntityBase>> retrievedAttachments)
    {
        Bundle intentArgs = intent.getBundleExtra(C_ATTACHMENT_LIST_WRAPPER_PROP_NAME);

        if (intentArgs == null)
            return new Error("No Attachments have been provided!", true);

        List<AttachmentEntityBase> attachmentList
                = (List<AttachmentEntityBase>) intentArgs
                .getSerializable(C_ATTACHMENT_LIST_PROP_NAME);

        if (attachmentList == null)
            return new Error("No Attachments have been provided!", true);

        retrievedAttachments.setValue(attachmentList);

        return null;
    }

    private void finishWithError(final Error error) {
        ErrorBroadcastReceiver
                .broadcastError(
                        error,
                        getApplicationContext());
        finish();
    }

    private void showChooser(final List<AttachmentEntityBase> attachmentList) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        android.R.id.content,
                        new AttachmentChooserFragment(attachmentList, this),
                        AttachmentChooserFragment.C_TAG)
                .commit();
    }

    private void showShower(final AttachmentEntityBase attachment) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(
                        android.R.id.content,
                        new AttachmentShowerFragment(attachment),
                        AttachmentShowerFragment.C_TAG)
                .commit();
    }

    @Override
    public void onAttachmentChosen(final AttachmentEntityBase chosenAttachment) {
        if (chosenAttachment == null) {
            finishWithError(new Error("No Attachment has been chosen!", true));

            return;
        }

        Error processingError = processAttachmentChoice(chosenAttachment);

        if (processingError != null) finishWithError(processingError);
    }

    private Error processAttachmentChoice(
            final AttachmentEntityBase chosenAttachment)
    {
        AttachmentType attachmentType = chosenAttachment.getType();

        switch (attachmentType) {
            case DOC: return processAttachmentChoiceDoc(chosenAttachment);
        }

        showShower(chosenAttachment);

        return null;
    }

    private Error processAttachmentChoiceDoc(
            final AttachmentEntityBase chosenAttachment)
    {
        if (!(chosenAttachment instanceof AttachmentEntityDoc))
            return new Error("Attachment Data was wrong!", true);

        AttachmentEntityDoc attachmentDoc = (AttachmentEntityDoc) chosenAttachment;
        Uri docUri = Uri.parse(attachmentDoc.getURI().toString());

        (new LinkedFileOpenerAsync(docUri, this, this)).execute();

        return null;
    }

    @Override
    public void onFileOpeningFail(Uri fileUri) {
        AttachmentDocUtility.showFileShowingFailedToast(this, fileUri);
    }

    @Override
    public void onFileOpeningError(Error error) {
        finishWithError(error);
    }
}
