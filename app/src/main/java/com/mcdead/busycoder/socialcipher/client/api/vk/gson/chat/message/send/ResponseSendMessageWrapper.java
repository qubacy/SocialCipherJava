package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.message.send;

import com.google.gson.annotations.SerializedName;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.Error;

public class ResponseSendMessageWrapper {
    public @SerializedName("result") long sentMessageId;
    public Error error;
}
