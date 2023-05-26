package com.mcdead.busycoder.socialcipher.client.api.vk.webinterface;

import static com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext.C_API_VERSION;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.list.ResponseChatAttachmentListWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.message.send.ResponseSendMessageWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIChat {
    @GET("messages.getChat?v=" + C_API_VERSION)
    Call<ResponseChatDataWrapper> getChatData(
            @Query("chat_id") long localChatId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getConversations?extended=1&fields=name&v=" + C_API_VERSION)
    Call<ResponseChatListWrapper> getChatList(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getHistory?v=" + C_API_VERSION)
    Call<ResponseChatContentWrapper> getChatContent(
            @Query("peer_id") long chatId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getHistoryAttachments?v=" + C_API_VERSION)
    Call<ResponseChatAttachmentListWrapper> getChatAttachmentList(
            @Query("peer_id") long chatId,
            @Query("media_type") String attachmentType,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.send?random_id=0&v=" + C_API_VERSION)
    Call<ResponseSendMessageWrapper> sendMessage(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("peer_id") long chatId,
            @Query("message") String messageText,
            @Query("attachment") String attachments
    );
}
