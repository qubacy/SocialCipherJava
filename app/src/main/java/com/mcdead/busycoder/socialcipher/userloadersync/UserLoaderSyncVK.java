package com.mcdead.busycoder.socialcipher.userloadersync;

import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.group.ResponseGroupBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.group.ResponseGroupContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.group.ResponseGroupWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.error.Error;

import java.io.IOException;

import retrofit2.Response;

public class UserLoaderSyncVK extends UserLoaderSyncBase {

    public UserLoaderSyncVK(
            final String token)
    {
        super(token);
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
        if (usersStore.getUserByPeerId(userId) != null
         || usersStore.getLocalUser().getPeerId() == userId)
        {
            return null;
        }

        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("API hasn't been initialized!", true);

        ResponseUserItem userData = null;

        try {
            Response<ResponseUserWrapper> userResponse =
                    vkAPI.user(userId, m_token).execute();

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
        UserEntity userEntity = new UserEntity(userData.id, userName);

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

        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("API hasn't been initialized!", true);

        ResponseGroupBody groupData = null;

        try {
            long positiveGroupId = -groupId;

            Response<ResponseGroupWrapper> groupResponse =
                    vkAPI.group(positiveGroupId, m_token).execute();

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
        UserEntity userEntity = new UserEntity(groupId, userName);

        if (!usersStore.addUser(userEntity))
            return new Error("New group user adding to storage operation has been failed!", true);

        return null;
    }
}
