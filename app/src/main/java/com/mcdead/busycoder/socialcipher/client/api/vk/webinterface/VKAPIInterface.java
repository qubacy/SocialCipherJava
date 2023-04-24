package com.mcdead.busycoder.socialcipher.client.api.vk.webinterface;

import static com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext.C_API_VERSION;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.list.ResponseChatAttachmentListWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.data.ResponseChatDataWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.content.ResponseChatContentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.message.send.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.list.ResponseChatListWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.group.ResponseGroupWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.video.ResponseVideoWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIInterface {
    @GET("users.get?v=" + C_API_VERSION)
    Call<ResponseUserWrapper> localUser(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("users.get?v=" + C_API_VERSION)
    Call<ResponseUserWrapper> user(
            @Query("user_ids") long userId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("groups.getById?v=" + C_API_VERSION)
    Call<ResponseGroupWrapper> group(
            @Query("group_id") long groupId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getChat?v=" + C_API_VERSION)
    Call<ResponseChatDataWrapper> chat(
            @Query("chat_id") long localChatId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getConversations?extended=1&fields=name&v=" + C_API_VERSION)
    Call<ResponseChatListWrapper> dialogs(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getHistory?v=" + C_API_VERSION)
    Call<ResponseChatContentWrapper> dialog(
            @Query("peer_id") long peerId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getHistoryAttachments?v=" + C_API_VERSION)
    Call<ResponseChatAttachmentListWrapper> getChatAttachments(
            @Query("peer_id") long peerId,
            @Query("media_type") String attachmentType,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("docs.getById?v=" + C_API_VERSION)
    Call<ResponseDocumentWrapper> document(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("docs") String docs
    );

    @GET("photos.getById?v=" + C_API_VERSION)
    Call<ResponsePhotoWrapper> photo(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("photos") String photos
    );

    @GET("video.get?v=" + C_API_VERSION)
    Call<ResponseVideoWrapper> video(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("videos") String videos
    );

    @GET("messages.getLongPollServer?v=" + C_API_VERSION)
    Call<ResponseLongPollServerWrapper> longPollServer(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.send?random_id=0&v=" + C_API_VERSION)
    Call<ResponseSendMessageWrapper> sendMessage(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("peer_id") long peerId,
            @Query("message") String messageText,
            @Query("attachment") String attachments
    );
}
