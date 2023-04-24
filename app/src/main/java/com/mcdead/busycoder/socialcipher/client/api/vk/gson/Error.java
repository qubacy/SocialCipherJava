package com.mcdead.busycoder.socialcipher.client.api.vk.gson;

import com.google.gson.annotations.SerializedName;

public class Error {
    public @SerializedName("error_msg") String message = null;

    public Error() {
        message = new String("");
    }

    public Error(final String message) {
        this.message = message;
    }
}
