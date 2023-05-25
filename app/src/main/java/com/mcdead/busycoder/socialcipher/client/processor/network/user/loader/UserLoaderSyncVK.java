package com.mcdead.busycoder.socialcipher.client.processor.user.loader;

import com.mcdead.busycoder.socialcipher.client.api.vk.gson.group.ResponseGroupBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.group.ResponseGroupContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.group.ResponseGroupWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.data.store.UsersStore;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.io.IOException;

import retrofit2.Response;

public class UserLoaderSyncVK extends UserLoaderSyncBase {
    final protected VKAPIProfile m_vkAPIProfile;

    protected UserLoaderSyncVK(
            final String token,
            final VKAPIProfile vkAPIProfile)
    {
        super(token);

        m_vkAPIProfile = vkAPIProfile;
    }

    public static UserLoaderSyncVK getInstance(
            final String token,
            final VKAPIProfile vkAPIProfile)
    {
        if (token == null || vkAPIProfile == null) return null;
        if (token.isEmpty()) return null;

        return new UserLoaderSyncVK(token, vkAPIProfile);
    }

    @Override
    public Error loadUserById(
            final long userId)
    {
        if (!ResponseUserContext.isUserId(userId))
            return null;

        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null)
            return new Error("Users Store hasn't been initialized!", true);
        if (usersStore.getUserByPeerId(userId) != null)
            return null;

        ResponseUserItem userData = null;

        try {
            Response<ResponseUserWrapper> userResponse =
                    m_vkAPIProfile.getUser(userId, m_token).execute();

            if (!userResponse.isSuccessful())
                return new Error("User Getting Request has been failed!", true);
            if (userResponse.body().error != null)
                return new Error(userResponse.body().error.message, true);
            if (userResponse.body().response.isEmpty())
                return new Error("User Getting request hasn't returned any user data!", true);

            userData = userResponse.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        String userName = userData.firstName + ' ' + userData.lastName;
        UserEntity userEntity =
                UserEntityGenerator.generateUserEntity(
                        userData.id, userName);

        if (userEntity == null)
            return new Error("New User's creation process has been failed!", true);

        if (!usersStore.addUser(userEntity))
            return new Error("New user adding to storage operation has been failed!", true);

        return null;
    }

    @Override
    public Error loadGroupById(final long groupId) {
        if (!ResponseGroupContext.isChatGroupId(groupId))
            return null;

        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null)
            return new Error("Users Store hasn't been initialized!", true);
        if (usersStore.getUserByPeerId(groupId) != null)
            return null;

        ResponseGroupBody groupData = null;

        try {
            long positiveGroupId = -groupId;

            Response<ResponseGroupWrapper> groupResponse =
                    m_vkAPIProfile.getGroup(positiveGroupId, m_token).execute();

            if (!groupResponse.isSuccessful())
                return new Error("User Getting Request has been failed!", true);
            if (groupResponse.body().error != null)
                return new Error(groupResponse.body().error.message, true);
            if (groupResponse.body().response.isEmpty())
                return new Error("User Getting request hasn't returned any user data!", true);

            groupData = groupResponse.body().response.get(0);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        String userName = groupData.name;
        UserEntity userEntity =
                UserEntityGenerator.generateUserEntity(groupId, userName);

        if (userEntity == null)
            return new Error("New User's creation process has been failed!", true);

        if (!usersStore.addUser(userEntity))
            return new Error("New group user adding to storage operation has been failed!", true);

        return null;
    }
}
