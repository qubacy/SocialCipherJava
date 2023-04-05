package com.mcdead.busycoder.socialcipher.api.vk.gson;

import com.google.gson.annotations.SerializedName;

public class Error {
    public @SerializedName("error_msg") String message;

    public Error(final String message) {
        this.message = message;
    }
}
