package com.mcdead.busycoder.socialcipher.client.activity.chat.fragment.requestanswerdialog;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.processor.service.data.RequestAnswer;

public interface RequestAnswerDialogFragmentCallback {
    public void onRequestAnswerDialogResultGotten(final RequestAnswer requestAnswer);
    public void onRequestAnswerDialogErrorOccurred(final Error error);
}
