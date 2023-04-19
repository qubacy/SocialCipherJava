package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.list;

import com.google.gson.annotations.SerializedName;

public class ResponseChatListItemConversation {
    public ResponseChatListItemConversationPeer peer;
    public @SerializedName("chat_settings")
    ResponseChatListItemSettings chatSettings;

}
