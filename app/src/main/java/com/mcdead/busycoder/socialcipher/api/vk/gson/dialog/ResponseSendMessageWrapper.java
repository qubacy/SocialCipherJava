package com.mcdead.busycoder.socialcipher.api.vk.gson.dialog;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.api.vk.gson.Error;

public class ResponseSendMessageWrapper {
    public @SerializedName("result") long sentMessageId;
    public Error error;
}
