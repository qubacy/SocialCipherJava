package com.mcdead.busycoder.socialcipher.client.processor.tokenchecker;

import android.os.Process;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
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
    final protected VKAPIProfile m_vkAPIProfile;

    protected TokenCheckerVK(
            final String token,
            final TokenCheckResultInterface callback,
            final VKAPIProfile vkAPIProfile)
    {
        super(token, callback);

        m_vkAPIProfile = vkAPIProfile;
    }

    public static TokenCheckerVK getInstance(
            final String token,
            final TokenCheckResultInterface callback,
            final VKAPIProfile vkAPIProfile)
    {
        if (token == null || vkAPIProfile == null) return null;
        if (token.isEmpty()) return null;

        return new TokenCheckerVK(token, callback, vkAPIProfile);
    }

    private Error getLocalUser(
            ObjectWrapper<TokenCheckResult> result)
            throws IOException
    {
        Response<ResponseUserWrapper> response =
                m_vkAPIProfile.
                        getLocalUser(m_token).
                        execute();

        if (!response.isSuccessful())
            return new Error(VKAPIContext.C_REQUEST_FAILED_MESSAGE, true);
        if (response.body().error != null)
            return new Error(response.body().error.message, false);
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

        ObjectWrapper<TokenCheckResult> resultWrapper = new ObjectWrapper<>();

        try {
           Error error = getLocalUser(resultWrapper);

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
        if (m_callback == null) return;

        if (result.isSucceeded())
            m_callback.onTokenCheckSuccess(result.getLocalUser());
        else
            m_callback.onTokenCheckFailure(result.getError());
    }
}
