package com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs;

import com.google.gson.annotations.SerializedName;

public class ResponseDialogsItemUserProfile {
    public long id;
    public @SerializedName("first_name") String firstName;
    public @SerializedName("last_name") String lastName;
}
