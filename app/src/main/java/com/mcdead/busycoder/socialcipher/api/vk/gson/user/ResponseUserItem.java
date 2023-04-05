package com.mcdead.busycoder.socialcipher.api.vk.gson.user;

import com.google.gson.annotations.SerializedName;

public class ResponseUserItem {
    public long id;
    public @SerializedName("first_name") String firstName;
    public @SerializedName("last_name") String lastName;
}
