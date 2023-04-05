package com.mcdead.busycoder.socialcipher.signin.tokenchecker;

import android.os.Process;

import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.data.entity.user.UserEntity;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;

import retrofit2.Response;

public class TokenCheckerVK extends TokenCheckerBase {
    public TokenCheckerVK(final String token,
                          TokenCheckResultInterface callback)
    {
        super(token, callback);
    }

    private Error getLocalUser(VKAPIInterface vkAPI,
                               ObjectWrapper<TokenCheckResult> result) throws IOException
    {
        if (vkAPI == null)
            return new Error("No API has been provided!", true);

        Response<ResponseUserWrapper> response = vkAPI
                .localUser(m_token)
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

        result.setValue(new TokenCheckResult(
                            new UserEntity(
                                    rawLocalUser.id,
                                    rawLocalUser.firstName + " " + rawLocalUser.lastName),
                            null,
                            true));

        return null;
    }

    @Override
    protected TokenCheckResult doInBackground(Void... voids) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new TokenCheckResult(
                    null,
                    new Error("API is not initialized!", true),
                    false);

        ObjectWrapper<TokenCheckResult> resultWrapper = new ObjectWrapper<>();

        try {
           Error error = getLocalUser(vkAPI, resultWrapper);

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
