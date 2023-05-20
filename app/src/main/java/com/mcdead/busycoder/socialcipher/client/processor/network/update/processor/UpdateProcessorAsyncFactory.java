package com.mcdead.busycoder.socialcipher.client.processor.network.update.processor;

import android.content.Context;

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
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        MessageProcessorBase messageProcessor = MessageProcessorStore.getProcessor();

        if (messageProcessor == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateUpdateProcessorVK(
                    settingsNetwork.getToken(), context, updateItemQueue, messageProcessor);
        }

        return null;
    }

    public static UpdateProcessorAsyncBase generateUpdateProcessorVK(
            final String token,
            final Context context,
            final LinkedBlockingQueue<ResponseUpdateItemInterface> updateItemQueue,
            final MessageProcessorBase messageProcessor)
    {
        ChatTypeDefinerVK chatTypeDefiner =
                (ChatTypeDefinerVK) ChatTypeDefinerFactory.generateDialogTypeDefiner();

        if (chatTypeDefiner == null)
            return null;

        UserLoaderSyncVK userLoader =
                (UserLoaderSyncVK) UserLoaderSyncFactory.generateUserLoader();

        if (userLoader == null)
            return null;

        VKAPIProvider vkAPIProvider = new VKAPIProvider();
        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return (UpdateProcessorAsyncBase)(new UpdateProcessorAsyncVK(
                token,
                context,
                updateItemQueue,
                chatTypeDefiner,
                userLoader,
                vkAPIChat,
                (MessageProcessorVK) messageProcessor));
    }
}
