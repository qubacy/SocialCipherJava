package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list;

import com.google.gson.annotations.SerializedName;

public class ResponseChatListItemUserProfile {
    public long id;
    public @SerializedName("first_name") String firstName;
    public @SerializedName("last_name") String lastName;
}
