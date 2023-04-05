package com.mcdead.busycoder.socialcipher.dialoglist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.RecyclerViewAdapterErrorCallback;
import com.mcdead.busycoder.socialcipher.data.entity.dialog.DialogEntity;
import com.mcdead.busycoder.socialcipher.error.Error;

import java.util.List;

public class DialogListAdapter extends RecyclerView.Adapter<DialogListViewHolder> {
    private LayoutInflater m_inflater = null;

    private RecyclerViewAdapterErrorCallback m_errorCallback = null;
    private DialogItemClickedCallback m_itemClickedCallback = null;

    private List<DialogEntity> m_dialogs = null;

    public DialogListAdapter(Activity activity,
                             RecyclerViewAdapterErrorCallback errorCallback,
                             DialogItemClickedCallback itemClickedCallback)
    {
        m_inflater = activity.getLayoutInflater();

        m_errorCallback = errorCallback;
        m_itemClickedCallback = itemClickedCallback;
    }

    public boolean setDialogsList(final List<DialogEntity> dialogs) {
        if (dialogs == null) return false;

        m_dialogs = dialogs;

        notifyDataSetChanged();

        return true;
    }

    @NonNull
    @Override
    public DialogListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType)
    {
        View view = m_inflater.inflate(R.layout.dialog_view_holder, parent, false);

        return new DialogListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogListViewHolder holder,
                                 int position)
    {
        if (m_dialogs == null) return;

        DialogEntity dialog = m_dialogs.get(position);

//        DialogEntity dialog = DialogsStore.getInstance().getDialogByIndex(position);
//
//        if (dialog == null) return;

        if (!holder.setDialogData(dialog)) {
            m_errorCallback.onRecyclerViewAdapterErrorOccurred(
                    new Error("View Holder setting error has been occurred!", true)
            );

            return;
        }

        holder.setItemClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEntity dialog = m_dialogs.get(holder.getAdapterPosition());

                if (dialog == null) {
                    m_errorCallback.onRecyclerViewAdapterErrorOccurred(
                            new Error("Dialog cannot be found!", true)
                    );

                    return;
                }

                m_itemClickedCallback.onDialogItemClick(dialog.getDialogId());
            }
        });
    }

    @Override
    public int getItemCount() {
        //return DialogsStore.getInstance().getDialogs().size();
        return (m_dialogs == null ? 0 : m_dialogs.size());
    }
}
