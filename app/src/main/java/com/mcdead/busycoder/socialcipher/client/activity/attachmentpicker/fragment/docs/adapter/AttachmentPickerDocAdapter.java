package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.docs.adapter;

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

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerDocAdapter extends RecyclerView.Adapter<AttachmentPickerDocViewHolder>
    implements
        AttachmentPickerDocViewHolderCallback
{
    final private LayoutInflater m_inflater;
    final private List<Pair<AttachmentData, ObjectWrapper<Boolean>>> m_docAttachmentDataList;

    private AttachmentPickerDocAdapterCallback m_callback = null;

    protected AttachmentPickerDocAdapter(
            final LayoutInflater layoutInflater,
            final AttachmentPickerDocAdapterCallback callback)
    {
        super();
        setHasStableIds(true);

        m_inflater = layoutInflater;
        m_callback = callback;

        m_docAttachmentDataList = new ArrayList<>();
    }

    public static AttachmentPickerDocAdapter getInstance(
            final LayoutInflater layoutInflater,
            final AttachmentPickerDocAdapterCallback callback)
    {
        if (layoutInflater == null) return null;

        return new AttachmentPickerDocAdapter(layoutInflater, callback);
    }

    public boolean setCallback(
            final AttachmentPickerDocAdapterCallback callback)
    {
        if (callback == null || m_callback != null) return false;

        m_callback = callback;

        return true;
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
                m_docAttachmentDataList.get(position);

        if (!holder.setData(
                docAttachmentItem.first.getFileName(),
                docAttachmentItem.second.getValue()))
        {
            if (m_callback == null) return;

            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
                    new Error("Doc Data setting has been occurred!", true)
            );
        }
    }

    @Override
    public int getItemCount() {
        return m_docAttachmentDataList.size();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    private boolean checkItemId(final int id) {
        if (id < 0 || m_docAttachmentDataList.size() <= id) {
//            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
//                    new Error("Wrong Image Id has been provided!", true)
//            );

            return false;
        }

        return true;
    }

    public boolean setDocList(
            final List<AttachmentData> docAttachmentDataList)
    {
        if (docAttachmentDataList == null) return false;

        for (final AttachmentData docAttachmentData : docAttachmentDataList) {
            m_docAttachmentDataList.add(new Pair<>(docAttachmentData, new ObjectWrapper<>(false)));
        }

        notifyDataSetChanged();

        return true;
    }

    public List<AttachmentData> getChosenDocList() {
        List<AttachmentData> chosenDocAttachmentDataList = new ArrayList<>();

        for (final Pair<AttachmentData, ObjectWrapper<Boolean>> docAttachmentItem : m_docAttachmentDataList) {
            if (docAttachmentItem.second.getValue())
                chosenDocAttachmentDataList.add(docAttachmentItem.first);
        }

        return chosenDocAttachmentDataList;
    }

    @Override
    public void onDocViewHolderDocClicked(final int position) {
        if (!checkItemId(position))
            return;

        Pair<AttachmentData, ObjectWrapper<Boolean>> docItem = m_docAttachmentDataList.get(position);

        docItem.second.setValue(!docItem.second.getValue());
    }

    @Override
    public void onDocViewHolderErrorOccurred(final Error error) {
        if (m_callback == null) return;

        if (error == null) {
            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
                    new Error("Error data hasn't been provided!", true)
            );

            return;
        }

        m_callback.onAttachmentPickerDocAdapterErrorOccurred(error);
    }
}
