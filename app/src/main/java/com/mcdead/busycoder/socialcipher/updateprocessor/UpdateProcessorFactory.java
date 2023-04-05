package com.mcdead.busycoder.socialcipher.updateprocessor;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.util.concurrent.LinkedBlockingQueue;

public class UpdateProcessorFactory {
    public static UpdateProcessorBase generateUpdateProcessor(
            Context context,
            LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue)
    {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        switch (settingsNetwork.getAPIType()) {
            case VK: return new UpdateProcessorVK(settingsNetwork.getToken(), context, updateItemQueue);
        }

        return null;
    }
}
