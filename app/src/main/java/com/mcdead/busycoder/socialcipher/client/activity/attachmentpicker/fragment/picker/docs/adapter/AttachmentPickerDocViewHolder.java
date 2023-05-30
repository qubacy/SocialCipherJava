package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;

public class AttachmentPickerDocViewHolder extends RecyclerView.ViewHolder {
    private boolean m_isChosen = false;

    private View m_itemView = null;

    private TextView m_fileNameView = null;

    private AttachmentPickerDocViewHolderCallback m_callback = null;
    private Resources m_resources = null;

    public AttachmentPickerDocViewHolder(
            @NonNull View itemView,
            @NonNull AttachmentPickerDocViewHolderCallback callback,
            @NonNull Context context)
    {
        super(itemView);

        m_itemView = itemView;

        m_fileNameView = itemView.findViewById(R.id.attachment_doc_view_holder_name);

        m_callback = callback;
        m_resources = context.getResources();
    }

    public boolean setData(
            final String fileName,
            final boolean isChosen)
    {
        if (fileName == null) return false;
        if (fileName.isEmpty()) return false;

        m_fileNameView.setText(fileName);

        changeItemChosenState(isChosen);

        m_itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDocItemClicked();
            }
        });

        return true;
    }

    private void onDocItemClicked() {
        changeItemChosenState(!m_isChosen);

        m_callback.onDocViewHolderDocClicked((int) getItemId());
    }

    private void changeItemChosenState(final boolean isChosen) {
        Drawable background = null;

        if (isChosen) {
            background =
                    ResourcesCompat.getDrawable(
                            m_resources,
                            R.drawable.attachment_doc_view_holder_chosen_background,
                            null);
        }

        m_itemView.setBackground(background);

        m_isChosen = isChosen;
    }
}
