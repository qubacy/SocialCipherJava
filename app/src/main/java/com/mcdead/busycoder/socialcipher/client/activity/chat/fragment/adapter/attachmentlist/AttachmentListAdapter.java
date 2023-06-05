package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.adapter.attachmentlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;

public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListViewHolder> {
    final private LayoutInflater m_layoutInflater;
    private AttachmentListAdapterCallback m_callback;

    protected AttachmentListAdapter(
            final LayoutInflater layoutInflater,
            final AttachmentListAdapterCallback callback)
    {
        super();

        m_layoutInflater = layoutInflater;
        m_callback = callback;
    }

    public static AttachmentListAdapter getInstance(
            final LayoutInflater layoutInflater,
            final AttachmentListAdapterCallback callback)
    {
        if (layoutInflater == null || callback == null)
            return null;

        return new AttachmentListAdapter(layoutInflater, callback);
    }

    @NonNull
    @Override
    public AttachmentListViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType)
    {
        View view =
                m_layoutInflater.inflate(
                        R.layout.fragment_chat_attachment_view_holder, parent, false);

        return new AttachmentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentListViewHolder holder, int position)
    {
        AttachmentData attachmentData = m_callback.getAttachmentByIndex(position);

        if (attachmentData == null) return;

        if (!holder.setData(attachmentData)) {
            // todo: handling an error..

            return;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_callback.onAttachmentClicked(attachmentData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_callback.getAttachmentListSize();
    }

    public void onDataSetChanged() {
        notifyDataSetChanged();
    }
}
