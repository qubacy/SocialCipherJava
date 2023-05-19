package com.mcdead.busycoder.socialcipher.client.processor.tokenchecker;

import android.os.Process;

import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntityGenerator;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResult;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResultInterface;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;

import retrofit2.Response;

public class TokenCheckerVK extends TokenCheckerBase {
    public TokenCheckerVK(final String token,
                          TokenCheckResultInterface callback)
    {
        super(token, callback);
    }

    private Error getLocalUser(final VKAPIProfile vkAPIProfile,
                               ObjectWrapper<TokenCheckResult> result) throws IOException
    {
        Response<ResponseUserWrapper> response = vkAPIProfile
                .getLocalUser(m_token)
                .execute();

        if (!response.isSuccessful())
            return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
        if (response.body().error != null)
            return new Error(response.body().error.message, true);
        if (response.body().response.isEmpty())
            return new Error("Local User has not been gotten", true);

        ResponseUserItem rawLocalUser = response.body().response.get(0);

        if (rawLocalUser == null)
            return new Error("Gotten Local User is null!", true);

        UserEntity userEntity =
                UserEntityGenerator.generateUserEntity(
                    rawLocalUser.id,
                    rawLocalUser.firstName + " " + rawLocalUser.lastName);

        if (userEntity == null)
            return new Error("New User's creation process has been failed!", true);

        result.setValue(
                new TokenCheckResult(
                    userEntity,
                    null,
                    true));

        return null;
    }

    @Override
    protected TokenCheckResult doInBackground(Void... voids) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        VKAPIProvider vkAPIProvider =
                (VKAPIProvider) APIProviderGenerator.generateAPIProvider();

        if (vkAPIProvider == null)
            return new TokenCheckResult(
                    null,
                    new Error("API hasn't been initialized!", true),
                    false);

        VKAPIProfile vkAPIProfile = vkAPIProvider.generateProfileAPI();
        ObjectWrapper<TokenCheckResult> resultWrapper = new ObjectWrapper<>();

        try {
           Error error = getLocalUser(vkAPIProfile, resultWrapper);

           if (error != null)
               return new TokenCheckResult(
                       null,
                       error,
                       false);

        } catch (IOException e) {
            e.printStackTrace();

            return new TokenCheckResult(
                    null,
                    new Error(VKAPIContext.C_REQUEST_CRASHED_MESSAGE, true),
                    false);
        }

        return resultWrapper.getValue();
    }

    @Override
    protected void onPostExecute(TokenCheckResult result) {
        if (result.isSucceeded())
            m_callback.onTokenCheckSuccess(result.getLocalUser());
        else
            m_callback.onTokenCheckFailure(result.getError());
    }
}
