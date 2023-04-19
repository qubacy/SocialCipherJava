package com.mcdead.busycoder.socialcipher.api.vk.webinterface;

import static com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext.C_API_VERSION;

import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.group.ResponseGroupWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIProfile {
    @GET("users.get?v=" + C_API_VERSION)
    Call<ResponseUserWrapper> getLocalUser(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("users.get?v=" + C_API_VERSION)
    Call<ResponseUserWrapper> getUser(
            @Query("user_ids") long userId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("groups.getById?v=" + C_API_VERSION)
    Call<ResponseGroupWrapper> getGroup(
            @Query("group_id") long groupId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );
}
