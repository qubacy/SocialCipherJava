package com.mcdead.busycoder.socialcipher.updatechecker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.error.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.longpoll.ResponseLongPollServerBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.UpdateDeserializer;
import com.mcdead.busycoder.socialcipher.dialoglist.DialogsBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;

public class UpdateCheckerVK extends UpdateCheckerBase {
    private static final String C_ACT_PROP_NAME = "act";
    private static final String C_KEY_PROP_NAME = "key";
    private static final String C_TS_PROP_NAME = "ts";
    private static final String C_WAIT_PROP_NAME = "wait";
    private static final String C_MODE_PROP_NAME = "mode";
    private static final String C_VERSION_PROP_NAME = "version";

    private static final String C_ACT_PROP_VALUE = "a_check";
    private static final int C_WAIT_PROP_VALUE = 25;
    private static final int C_MODE_PROP_VALUE = 2;
    private static final int C_VERSION_PROP_VALUE = 3;

    private static final int C_TIMEOUT_SEC_VALUE = 30;

    public UpdateCheckerVK(final String token,
                           Context context)
    {
        super(token, context);
    }

    private Error initLongPollServer(
            VKAPIInterface vkAPI,
            LongPollServerRequestResult requestResult)
    {
        try {
            Response<ResponseLongPollServerWrapper> response = vkAPI.longPollServer(m_token).execute();

            if (!response.isSuccessful())
                return new Error("vkAPI object is null!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

            requestResult.setResponse(response.body().response);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null) {
            sendErrorBroadcast(new Error("API obj. is not initialized!", true));

            return;
        }

        LongPollServerRequestResult longPollServerRequestResult = new LongPollServerRequestResult();
        Error longPollServerRequestError = initLongPollServer(vkAPI, longPollServerRequestResult);

        if (longPollServerRequestError != null) {
            sendErrorBroadcast(longPollServerRequestError);

            return;
        }

        final String server = longPollServerRequestResult.getResponse().server;
        final String key = longPollServerRequestResult.getResponse().key;
        long ts = longPollServerRequestResult.getResponse().ts;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .writeTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .readTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .build();

        while (!Thread.interrupted()) {
            SystemClock.sleep(VKAPIContext.C_REQUEST_TIMEOUT);

            Request request = new Request.Builder()
                    .url(generateLongPollUrl(server, key, ts))
                    .build();

            try (okhttp3.Response response = client.newCall(request).execute()) {
                ObjectWrapper<ResponseUpdateBody> updateBodyWrapper = new ObjectWrapper<>();

                Error updateError = getResponseUpdateBody(response, updateBodyWrapper);

                if (updateError != null) {
                    sendErrorBroadcast(updateError);

                    return;
                }

                ts = updateBodyWrapper.getValue().ts;

                if (!updateBodyWrapper.getValue().updates.isEmpty())
                    sendUpdateReceivedBroadcast(updateBodyWrapper.getValue().updates);
                    //m_receiver.onUpdatesReceived(updateBodyWrapper.getValue().updates);

            } catch (Throwable e) {
                e.printStackTrace();
                sendErrorBroadcast(new Error(e.getMessage(), true));

                return;
            }
        }
    }

    private void sendUpdateReceivedBroadcast(final List<ResponseUpdateItem> updates) {
        Intent intent = new Intent(DialogsBroadcastReceiver.C_UPDATES_RECEIVED);
        Bundle updatesBundle = new Bundle();

        updatesBundle.putSerializable(DialogsBroadcastReceiver.C_UPDATES_LIST_EXTRA_PROP_NAME, (Serializable) updates);
        intent.putExtra(DialogsBroadcastReceiver.C_UPDATES_WRAPPER_EXTRA_PROP_NAME, updatesBundle);

        LocalBroadcastManager.getInstance(m_context.getApplicationContext()).sendBroadcast(intent);
    }

    private void sendErrorBroadcast(final Error error) {
        ErrorBroadcastReceiver.broadcastError(error, m_context.getApplicationContext());
//
//        Intent intent = new Intent(m_context.getApplicationContext(), ErrorBroadcastReceiver.class)
//                .setAction(ErrorBroadcastReceiver.C_ERROR_RECEIVED);
//
//        intent.putExtra(ErrorBroadcastReceiver.C_ERROR_EXTRA_PROP_NAME, error);
//
//        LocalBroadcastManager.getInstance(m_context).sendBroadcast(intent);
    }

    private Error getResponseUpdateBody(final okhttp3.Response response,
                                        ObjectWrapper<ResponseUpdateBody> updateBodyWrapper)
            throws IOException, JSONException
    {
        ResponseUpdateBody updateBodyBuf = deserializeUpdate(response.body().string());

        if (!response.isSuccessful() || updateBodyBuf == null)
            return new Error("Updates getting error!", true);
        if (updateBodyBuf.updates == null)
            return new Error("Updates getting error!", true);

        updateBodyWrapper.setValue(updateBodyBuf);

        return null;
    }

    private ResponseUpdateBody deserializeUpdate(
            final String rawJsonUpdate)
            throws JSONException
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                ResponseUpdateBody.class,
                new UpdateDeserializer()).create();

        return gson.fromJson(rawJsonUpdate, ResponseUpdateBody.class);
    }

    private String generateLongPollUrl(final String server,
                                       final String key,
                                       final long ts)
    {
        return "https://" + server + '?'
             + C_ACT_PROP_NAME + '=' + C_ACT_PROP_VALUE + '&'
             + C_KEY_PROP_NAME + '=' + key + '&'
             + C_TS_PROP_NAME + '=' + String.valueOf(ts) + '&'
             + C_WAIT_PROP_NAME + '=' + String.valueOf(C_WAIT_PROP_VALUE) + '&'
             + C_MODE_PROP_NAME + '=' + String.valueOf(C_MODE_PROP_VALUE) + '&'
             + C_VERSION_PROP_NAME + '=' + String.valueOf(C_VERSION_PROP_VALUE);

    }

    private static class LongPollServerRequestResult {
        private ResponseLongPollServerBody m_response = null;

        public LongPollServerRequestResult() {

        }

        public ResponseLongPollServerBody getResponse() {
            return m_response;
        }

        public boolean setResponse(final ResponseLongPollServerBody response) {
            if (response == null || m_response != null)
                return false;

            m_response = response;

            return true;
        }
    }
}
