package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswerType;

public class RequestAnswerDialogFragment extends DialogFragment {
    public static final String C_FRAGMENT_TAG = "requestAnswerDialog";

    final private RequestAnswerType m_requestAnswerType;
    final private String m_requestText;

    final private RequestAnswerDialogFragmentCallback m_callback;

    private RequestAnswerDialogFragment(
            final RequestAnswerType requestAnswerType,
            final String requestText,
            final RequestAnswerDialogFragmentCallback callback)
    {
        m_requestAnswerType = requestAnswerType;
        m_requestText = requestText;

        m_callback = callback;
    }

    public static RequestAnswerDialogFragment getInstance(
            final RequestAnswerType requestAnswerType,
            final String requestText,
            final RequestAnswerDialogFragmentCallback callback)
    {
        if (callback == null || requestAnswerType == null)
            return null;

        RequestAnswerDialogFragment requestAnswerDialogFragment =
                new RequestAnswerDialogFragment(requestAnswerType, requestText, callback);

        return requestAnswerDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        View view = null;

        switch (m_requestAnswerType) {
            case SETTING_CIPHER_SESSION:
                view = createBooleanRequestAnswerView(inflater, container); break;
        }

        if (view == null) {
            m_callback.onRequestAnswerDialogErrorOccurred(
                    new Error("Provided Request Answer Type is unknown!", true)
            );

            return null;
        }

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private View createBooleanRequestAnswerView(
            final LayoutInflater inflater,
            final ViewGroup container)
    {
        View view = inflater.inflate(
                R.layout.dialog_fragment_request_answer_boolean,
                container,
                false);

        TextView text = view.findViewById(R.id.request_answer_boolean_text);
        Button acceptButton = view.findViewById(R.id.request_answer_boolean_accept);
        Button cancelButton = view.findViewById(R.id.request_answer_boolean_cancel);

        if (text == null || acceptButton == null || cancelButton == null)
            return null;

        text.setText(m_requestText);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CipherRequestAnswerSettingSession requestAnswer =
                        new CipherRequestAnswerSettingSession(true);

                processRequestAnswerResult(requestAnswer);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CipherRequestAnswerSettingSession requestAnswer =
                        new CipherRequestAnswerSettingSession(false);

                processRequestAnswerResult(requestAnswer);
            }
        });

        return view;
    }

    private void processRequestAnswerResult(
            final RequestAnswer requestAnswer)
    {
        m_callback.onRequestAnswerDialogResultGotten(requestAnswer);
    }
}
