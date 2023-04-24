package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseChatDataBody {
    public String title;
    public @SerializedName("users") List<Long> userIdList;
}
