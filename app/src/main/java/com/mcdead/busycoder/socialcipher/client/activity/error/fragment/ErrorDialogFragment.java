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
import androidx.lifecycle.ViewModelProvider;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.fragment.model.ErrorDialogFragmentViewModel;

public class ErrorDialogFragment extends Fragment {
    private static final String C_ERROR_ARG_NAME = "error";
    private static final String C_CALLBACK_ARG_NAME = "callback";

    private ErrorDialogFragmentViewModel m_errorDialogFragmentViewModel = null;

    public ErrorDialogFragment() {
        super();
    }

    protected ErrorDialogFragment(
            final Bundle args)
    {
        super();

        setArguments(args);
    }

    public static ErrorDialogFragment getInstance(
            final Error error,
            final ErrorFragmentCallback callback)
    {
        if (error == null || callback == null)
            return null;

        Bundle args = new Bundle();

        args.putSerializable(C_ERROR_ARG_NAME, error);
        args.putSerializable(C_CALLBACK_ARG_NAME, callback);

        return new ErrorDialogFragment(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_errorDialogFragmentViewModel =
                new ViewModelProvider(this).get(ErrorDialogFragmentViewModel.class);

        if (!m_errorDialogFragmentViewModel.isInitialized()) {
            Bundle args = getArguments();

            if (args == null) {

                return;
            }

            Error error = (Error) args.getSerializable(C_ERROR_ARG_NAME);
            ErrorFragmentCallback errorFragmentCallback =
                    (ErrorFragmentCallback) args.getSerializable(C_CALLBACK_ARG_NAME);

            if (!m_errorDialogFragmentViewModel.setError(error)) {

                return;
            }

            if (!m_errorDialogFragmentViewModel.setCallback(errorFragmentCallback)) {

                return;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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

        errorTextView.setText(m_errorDialogFragmentViewModel.getError().getMessage());
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked();
            }
        });

        return view;
    }

    private void onButtonClicked() {
        m_errorDialogFragmentViewModel.getCallback().onErrorClosed();
    }
}
