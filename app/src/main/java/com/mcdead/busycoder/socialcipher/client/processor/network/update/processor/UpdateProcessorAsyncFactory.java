package com.mcdead.busycoder.socialcipher.client.processor.network.update.processor;

import android.content.Context;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorStore;

import java.util.concurrent.LinkedBlockingQueue;

public class UpdateProcessorAsyncFactory {
    public static UpdateProcessorAsyncBase generateUpdateProcessor(
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue)
    {
        if (!checkCommonArgsValidity(context, updateItemQueue))
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        MessageProcessorBase messageProcessor = MessageProcessorStore.getProcessor();

        if (messageProcessor == null)
            return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (UpdateProcessorAsyncBase) generateUpdateProcessorVK(
                    settingsNetwork.getToken(),
                    context,
                    updateItemQueue,
                    (MessageProcessorVK) messageProcessor,
                    (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static UpdateProcessorAsyncVK generateUpdateProcessorVK(
            final String token,
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue,
            final MessageProcessorVK messageProcessorVK,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, context, updateItemQueue,
                messageProcessorVK, vkAPIProvider))
        {
            return null;
        }

        ChatTypeDefinerVK chatTypeDefiner =
                (ChatTypeDefinerVK) ChatTypeDefinerFactory.generateDialogTypeDefiner();

        if (chatTypeDefiner == null)
            return null;

        UserLoaderSyncVK userLoader =
                (UserLoaderSyncVK) UserLoaderSyncFactory.generateUserLoader();

        if (userLoader == null)
            return null;

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return new UpdateProcessorAsyncVK(
                token,
                context,
                updateItemQueue,
                chatTypeDefiner,
                userLoader,
                vkAPIChat,
                messageProcessorVK);
    }

    private static boolean checkCommonArgsValidity(
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue)
    {
        if (context == null || updateItemQueue == null)
            return false;

        return true;
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue,
            final MessageProcessorBase messageProcessor,
            final APIProvider apiProvider)
    {
        if (!checkCommonArgsValidity(context, updateItemQueue) ||
            token == null || messageProcessor == null || apiProvider == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }
}
