package com.mcdead.busycoder.socialcipher.client.activity.signin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInCallback;
import com.mcdead.busycoder.socialcipher.client.activity.signin.data.SignInData;

public class SignInTokenFragment extends Fragment {
    private EditText m_tokenEditText;

    final private SignInCallback m_callback;

    public SignInTokenFragment(
            final SignInCallback callback)
    {
        m_callback = callback;
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
        View view = inflater.inflate(R.layout.fragment_signin_token, container, false);

        m_tokenEditText = view.findViewById(R.id.fragment_signin_token_text);

        Button acceptButton = view.findViewById(R.id.fragment_signin_token_accept_button);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAcceptButtonClicked();
            }
        });

        return view;
    }

    private void onAcceptButtonClicked() {
        String tokenString = m_tokenEditText.getText().toString();

        if (tokenString.isEmpty()) {
            Toast.makeText(getContext(), "Enter your token!", Toast.LENGTH_LONG);

            return;
        }

        m_callback.processData(new SignInData(tokenString));
    }
}
