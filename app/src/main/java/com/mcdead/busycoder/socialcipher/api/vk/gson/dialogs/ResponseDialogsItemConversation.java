package com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs;

import com.google.gson.annotations.SerializedName;

public class ResponseDialogsItemConversation {
    public ResponseDialogsItemConversationPeer peer;
    public @SerializedName("chat_settings") ResponseDialogsItemChatSettings chatSettings;

}
