package com.mcdead.busycoder.socialcipher.client.processor.update.checker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.activity.error.broadcastreceiver.ErrorBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.longpoll.ResponseLongPollServerBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.ResponseUpdateItem;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.UpdateDeserializer;
import com.mcdead.busycoder.socialcipher.client.activity.chatlist.broadcastreceiver.ChatListBroadcastReceiver;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;

public class UpdateCheckerAsyncVK extends UpdateCheckerAsyncBase {
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

    final protected VKAPIAttachment m_vkAPIAttachment;

    private Call m_curCall = null;

    protected UpdateCheckerAsyncVK(
            final String token,
            final Context context,
            final VKAPIAttachment vkAPIAttachment)
    {
        super(token, context);

        m_vkAPIAttachment = vkAPIAttachment;
    }

    public static UpdateCheckerAsyncVK getInstance(
            final String token,
            final Context context,
            final VKAPIAttachment vkAPIAttachment)
    {
        if (token == null || context == null || vkAPIAttachment == null)
            return null;
        if (token.isEmpty()) return null;

        return new UpdateCheckerAsyncVK(token, context, vkAPIAttachment);
    }

    private Error initLongPollServer(
            LongPollServerRequestResult requestResult)
    {
        try {
            Response<ResponseLongPollServerWrapper> response =
                    m_vkAPIAttachment.getLongPollServer(m_token).execute();

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

    private Error execUpdateChecking() {
        LongPollServerRequestResult longPollServerRequestResult = new LongPollServerRequestResult();
        Error longPollServerRequestError = initLongPollServer(longPollServerRequestResult);

        if (longPollServerRequestError != null)
            return longPollServerRequestError;

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

            Request request =
                    new Request.Builder().
                            url(generateLongPollUrl(server, key, ts)).
                            build();

            m_curCall = client.newCall(request);

            try (okhttp3.Response response = m_curCall.execute()) {
                ObjectWrapper<ResponseUpdateBody> updateBodyWrapper = new ObjectWrapper<>();

                Error updateError = getResponseUpdateBody(response, updateBodyWrapper);

                if (updateError != null) return updateError;

                if (updateBodyWrapper.getValue() == null)
                    continue;

                ts = updateBodyWrapper.getValue().ts;

                if (!updateBodyWrapper.getValue().updates.isEmpty())
                    sendUpdateReceivedBroadcast(updateBodyWrapper.getValue().updates);

            } catch (InterruptedIOException e) {
                e.printStackTrace();

                // todo: how to handle?

                return null;

            }
            catch (JSONException e) {
                e.printStackTrace();

                return new Error(e.getMessage(), true);
            }
            catch (IOException e) {
                e.printStackTrace();

                // todo: it needs to be defined fully.
                //return new Error(e.getMessage(), true);

                return null;
            }
        }

        return null;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Error checkingUpdateError = execUpdateChecking();

        Log.d("TEST", "Checker is out of the game!");

        if (checkingUpdateError != null)
            sendErrorBroadcast(checkingUpdateError);
    }

    @Override
    public void interruptChecking() {
        if (m_curCall != null)
            m_curCall.cancel();
    }

    private void sendUpdateReceivedBroadcast(final List<ResponseUpdateItem> updates) {
        Intent intent = new Intent(ChatListBroadcastReceiver.C_UPDATES_RECEIVED);
        Bundle updatesBundle = new Bundle();

        updatesBundle.putSerializable(ChatListBroadcastReceiver.C_UPDATES_LIST_EXTRA_PROP_NAME, (Serializable) updates);
        intent.putExtra(ChatListBroadcastReceiver.C_UPDATES_WRAPPER_EXTRA_PROP_NAME, updatesBundle);

        if (Thread.currentThread().isInterrupted()) return;

        LocalBroadcastManager.getInstance(m_context.getApplicationContext()).sendBroadcast(intent);
    }

    private void sendErrorBroadcast(final Error error) {
        ErrorBroadcastReceiver.broadcastError(error, m_context.getApplicationContext());
    }

    private Error getResponseUpdateBody(final okhttp3.Response response,
                                        ObjectWrapper<ResponseUpdateBody> updateBodyWrapper)
            throws IOException, JSONException
    {
        if (!response.isSuccessful())
            return new Error("Updates getting error!", true);

        ResponseUpdateBody updateBodyBuf = deserializeUpdate(response.body().string());

        if (updateBodyBuf == null)
            return null;
        if (updateBodyBuf.updates == null)
            return new Error("Updates deserializing error has been occurred!", true);

        updateBodyWrapper.setValue(updateBodyBuf);

        return null;
    }

    private ResponseUpdateBody deserializeUpdate(
            final String rawJsonUpdate)
            throws JSONException
    {
        Gson gson =
            new GsonBuilder().registerTypeAdapter(
                ResponseUpdateBody.class,
                new UpdateDeserializer()).create();

        return gson.fromJson(rawJsonUpdate, ResponseUpdateBody.class);
    }

    private String generateLongPollUrl(
            final String server,
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
