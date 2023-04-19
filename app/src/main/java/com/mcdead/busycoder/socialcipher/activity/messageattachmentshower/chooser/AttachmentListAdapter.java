package com.mcdead.busycoder.socialcipher.activity.messageattachmentshower.chooser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.activity.error.data.Error;

import java.util.List;

public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListViewHolder> {
    private List<AttachmentEntityBase> m_attachmentList = null;
    private LayoutInflater m_inflater = null;
    private AttachmentListAdapterCallback m_callback = null;

    public AttachmentListAdapter(
            final List<AttachmentEntityBase> attachmentList,
            LayoutInflater inflater,
            AttachmentListAdapterCallback callback)
    {
        m_attachmentList = attachmentList;
        m_inflater = inflater;
        m_callback = callback;
    }

    @NonNull
    @Override
    public AttachmentListViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder = m_inflater.inflate(R.layout.attachment_view_holder, parent, false);

        return new AttachmentListViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentListViewHolder holder,
            int position)
    {
        if (position >= m_attachmentList.size()) return;

        AttachmentEntityBase attachment = m_attachmentList.get(position);

        if (!holder.setData(attachment)) {
            m_callback.onAttachmentListError(
                    new Error(
                            "Setting Attachment Data process has gone wrong!",
                            true)
            );

            return;
        }

        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_callback.onAttachmentChosen(attachment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (m_attachmentList == null ? 0 : m_attachmentList.size());
    }
}
