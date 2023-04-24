package com.mcdead.busycoder.socialcipher.client.activity.error.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;

public class ErrorDialogFragment extends Fragment {
    private Error m_error = null;
    private ErrorFragmentCallback m_callback = null;

    public ErrorDialogFragment(
            final Error error,
            ErrorFragmentCallback callback)
    {
        m_error = error;
        m_callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) return;

        m_error = (Error) savedInstanceState.getSerializable(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME, m_error);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_error, container, false);

        TextView errorTextView = view.findViewById(R.id.error_text);
        Button errorButton = view.findViewById(R.id.error_button);

        errorTextView.setText(m_error.getMessage());
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked();
            }
        });

        return view;
    }

    private void onButtonClicked() {
        m_callback.onErrorClosed();

//        if (m_error.isCritical())
//            System.exit(-1);
    }
}
