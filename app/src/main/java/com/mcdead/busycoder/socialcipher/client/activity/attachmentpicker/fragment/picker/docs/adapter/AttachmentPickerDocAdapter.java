package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.docs.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class AttachmentPickerDocAdapter extends RecyclerView.Adapter<AttachmentPickerDocViewHolder>
    implements
        AttachmentPickerDocViewHolderCallback
{
    final private LayoutInflater m_inflater;

    final private AttachmentPickerDocAdapterCallback m_callback;

    protected AttachmentPickerDocAdapter(
            final LayoutInflater layoutInflater,
            final AttachmentPickerDocAdapterCallback callback)
    {
        super();
        setHasStableIds(true);

        m_inflater = layoutInflater;
        m_callback = callback;
    }

    public static AttachmentPickerDocAdapter getInstance(
            final LayoutInflater layoutInflater,
            final AttachmentPickerDocAdapterCallback callback)
    {
        if (layoutInflater == null || callback == null) return null;

        return new AttachmentPickerDocAdapter(layoutInflater, callback);
    }

    @NonNull
    @Override
    public AttachmentPickerDocViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder =
                m_inflater.inflate(R.layout.attachment_doc_view_holder, parent, false);

        return new AttachmentPickerDocViewHolder(
                viewHolder,
                this,
                m_inflater.getContext());
    }

    @Override
    public void onBindViewHolder(
            @NonNull AttachmentPickerDocViewHolder holder,
            int position)
    {
        if (!checkItemId(position))
            return;

        Pair<AttachmentData, ObjectWrapper<Boolean>> docAttachmentItem =
                m_callback.getDocAttachmentDataByIndex(holder.getAdapterPosition());

        if (!holder.setData(
                docAttachmentItem.first.getFileName(),
                docAttachmentItem.second.getValue()))
        {
            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
                    new Error("Doc Data setting has been occurred!", true)
            );
        }
    }

    @Override
    public int getItemCount() {
        return m_callback.getDocAttachmentDataListSize();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    private boolean checkItemId(final int id) {
        if (id < 0 || m_callback.getDocAttachmentDataListSize() <= id) {
//            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
//                    new Error("Wrong Image Id has been provided!", true)
//            );

            return false;
        }

        return true;
    }

    @Override
    public void onDocViewHolderDocClicked(final int position) {
        if (!checkItemId(position))
            return;

        m_callback.onDocAttachmentDataChosenStateChanged(position);
    }

    @Override
    public void onDocViewHolderErrorOccurred(final Error error) {
        if (error == null) {
            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
                    new Error("Error data hasn't been provided!", true)
            );

            return;
        }

        m_callback.onAttachmentPickerDocAdapterErrorOccurred(error);
    }
}
