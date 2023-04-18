package com.mcdead.busycoder.socialcipher.api.vk.gson.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseChatBody {
    public String title;
    public @SerializedName("users") List<Long> userIdList;
}
