package com.mcdead.busycoder.socialcipher.attachmentpicker.docs;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class AttachmentPickerDocAdapter extends RecyclerView.Adapter<AttachmentPickerDocViewHolder>
    implements
        AttachmentPickerDocViewHolderCallback
{
    private LayoutInflater m_inflater = null;
    private List<Pair<DocData, ObjectWrapper<Boolean>>> m_docDataList = null;

    private AttachmentPickerDocAdapterCallback m_callback = null;

    public AttachmentPickerDocAdapter(
            @NonNull Context context,
            @NonNull AttachmentPickerDocAdapterCallback callback)
    {
        super();
        setHasStableIds(true);

        m_inflater = LayoutInflater.from(context);
        m_docDataList = new ArrayList<>();

        m_callback = callback;
    }

    @NonNull
    @Override
    public AttachmentPickerDocViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType)
    {
        View viewHolder = m_inflater.inflate(R.layout.attachment_doc_view_holder, parent, false);

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

        Pair<DocData, ObjectWrapper<Boolean>> docItem = m_docDataList.get(position);

        if (!holder.setData(
                docItem.first.getDisplayName(),
                docItem.second.getValue()))
        {
            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
                    new Error("Doc Data setting has been occurred!", true)
            );
        }
    }

    @Override
    public int getItemCount() {
        return m_docDataList.size();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    private boolean checkItemId(final int id) {
        if (id < 0 || m_docDataList.size() <= id) {
//            m_callback.onAttachmentPickerDocAdapterErrorOccurred(
//                    new Error("Wrong Image Id has been provided!", true)
//            );

            return false;
        }

        return true;
    }

    public boolean setDocList(
            final List<DocData> docDataList)
    {
        if (docDataList == null) return false;

        for (final DocData docData : docDataList) {
            m_docDataList.add(new Pair<>(docData, new ObjectWrapper<>(false)));
        }

        notifyDataSetChanged();

        return true;
    }

    public List<Uri> getChosenDocList() {
        List<Uri> chosenDocDataList = new ArrayList<>();

        for (final Pair<DocData, ObjectWrapper<Boolean>> docItem : m_docDataList) {
            if (docItem.second.getValue())
                chosenDocDataList.add(docItem.first.getContentUri());
        }

        return chosenDocDataList;
    }

    @Override
    public void onDocViewHolderDocClicked(final int position) {
        if (!checkItemId(position))
            return;

        Pair<DocData, ObjectWrapper<Boolean>> docItem = m_docDataList.get(position);

        docItem.second.setValue(!docItem.second.getValue());
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
