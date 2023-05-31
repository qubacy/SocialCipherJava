package com.mcdead.busycoder.socialcipher.client.activity.messageattachmentshower.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.util.List;

public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListViewHolder> {
    final private List<AttachmentEntityBase> m_attachmentToShowList;
    final private LayoutInflater m_inflater;
    final private AttachmentListAdapterCallback m_callback;

    protected AttachmentListAdapter(
            final List<AttachmentEntityBase> attachmentToShowList,
            final LayoutInflater inflater,
            final AttachmentListAdapterCallback callback)
    {
        m_attachmentToShowList = attachmentToShowList;
        m_inflater = inflater;
        m_callback = callback;
    }

    public static AttachmentListAdapter getInstance(
            final List<AttachmentEntityBase> attachmentList,
            final LayoutInflater inflater,
            final AttachmentListAdapterCallback callback)
    {
        if (attachmentList == null || inflater == null || callback == null)
            return null;

        return new AttachmentListAdapter(attachmentList, inflater, callback);
    }

    @NonNull
    @Override
    public AttachmentListViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder =
                m_inflater.inflate(R.layout.attachment_shower_view_holder, parent, false);
        AttachmentListViewHolder attachmentListViewHolder =
                AttachmentListViewHolder.getInstance(viewHolder);

        if (attachmentListViewHolder == null) {
            m_callback.onAttachmentListError(
                    new Error(
                            "Cannot create a View Holder for the attachment!",
                            true));
        }

        return attachmentListViewHolder;
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentListViewHolder holder,
            int position)
    {
        if (position >= m_attachmentToShowList.size()) return;

        AttachmentEntityBase attachmentDataToShow = m_attachmentToShowList.get(position);
        boolean isChosen = (m_callback.getLastChosenAttachment() == position);

        if (!holder.setData(attachmentDataToShow, isChosen)) {
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
                if (m_callback.onAttachmentChosen(
                        attachmentDataToShow, holder.getAdapterPosition()))
                {
                    holder.setActiveState(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (m_attachmentToShowList == null ? 0 : m_attachmentToShowList.size());
    }
}
