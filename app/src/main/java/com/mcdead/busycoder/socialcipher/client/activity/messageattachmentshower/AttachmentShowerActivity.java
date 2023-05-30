package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.adapter.AttachmentListAdapter;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.adapter.AttachmentListAdapterCallback;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.model.AttachmentShowerViewModel;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.AttachmentDocUtility;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.LinkedFileOpenerAsync;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.doc.LinkedFileOpenerCallback;
import com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.fragment.AttachmentShowerFragment;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityDoc;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentType;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentShowerActivity extends AppCompatActivity
    implements
        LinkedFileOpenerCallback,
        AttachmentListAdapterCallback
{
    public static final String C_ATTACHMENT_LIST_WRAPPER_PROP_NAME = "attachmentListWrapper";
    public static final String C_ATTACHMENT_LIST_PROP_NAME = "attachmentList";

    private AttachmentShowerViewModel m_attachmentChooserViewModel = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attachment_shower);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setTitle(R.string.attachment_shower_action_bar_title);
        }

        m_attachmentChooserViewModel =
                new ViewModelProvider(this).get(AttachmentShowerViewModel.class);

        if (!m_attachmentChooserViewModel.isInitialized()) {
            ObjectWrapper<List<AttachmentEntityBase>> attachmentListWrapper = new ObjectWrapper<>();
            Error retrievingError =
                    retrieveAttachmentsFromIntent(getIntent(), attachmentListWrapper);

            if (retrievingError != null) {
                finishWithError(retrievingError);

                return;
            }

            if (!m_attachmentChooserViewModel.setAttachmentList(attachmentListWrapper.getValue())) {
                finishWithError(
                        new Error("Attachment List hasn't been set!", true));

                return;
            }
        }

        RecyclerView attachmentListView = findViewById(R.id.attachment_chooser_list);
        AttachmentListAdapter attachmentListAdapter =
                new AttachmentListAdapter(
                        m_attachmentChooserViewModel.getAttachmentList(),
                        getLayoutInflater(),
                        this);

        attachmentListView.setLayoutManager(new LinearLayoutManager(this));
        attachmentListView.setAdapter(attachmentListAdapter);
    }

    private Error retrieveAttachmentsFromIntent(
            final Intent intent,
            ObjectWrapper<List<AttachmentEntityBase>> retrievedAttachments)
    {
        Bundle intentArgs = intent.getBundleExtra(C_ATTACHMENT_LIST_WRAPPER_PROP_NAME);

        if (intentArgs == null)
            return new Error("No Attachments have been provided!", true);

        List<AttachmentEntityBase> attachmentList =
                (List<AttachmentEntityBase>) (intentArgs
                    .getSerializable(C_ATTACHMENT_LIST_PROP_NAME));

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

    private void showShower(final AttachmentEntityBase attachment) {
        AttachmentShowerFragment attachmentShowerFragment =
                AttachmentShowerFragment.getInstance(attachment);

        if (attachmentShowerFragment == null) {
            ErrorBroadcastReceiver.broadcastError(
                    new Error(
                            "Attachment Shower Fragment hasn't been initialized!",
                            true),
                    getApplicationContext());

            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(
                        android.R.id.content,
                        attachmentShowerFragment,
                        AttachmentShowerFragment.C_TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


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
        Uri docUri = Uri.parse(attachmentDoc.getURIBySize(AttachmentSize.STANDARD).toString());

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

    @Override
    public void onAttachmentListError(final Error error) {
        if (error == null) {
            ErrorBroadcastReceiver
                    .broadcastError(
                            new Error("Error Data hasn't been provided", true),
                            getApplicationContext());

            return;
        }

        ErrorBroadcastReceiver
                .broadcastError(error, getApplicationContext());
    }

    @Override
    public void onAttachmentChosen(AttachmentEntityBase attachment) {
        if (attachment == null) {
            finishWithError(new Error("No Attachment has been chosen!", true));

            return;
        }

        Error processingError = processAttachmentChoice(attachment);

        if (processingError != null) finishWithError(processingError);
    }
}
