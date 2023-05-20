package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;

public class ChatLoaderFactory {
    public static ChatLoaderBase generateChatLoader(
            final ChatLoadingCallback callback,
            final long chatId)
    {
        if (callback == null || chatId == 0) return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        MessageProcessorBase messageProcessor = MessageProcessorStore.getProcessor();

        if (messageProcessor == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateChatLoaderVK(
                    settingsNetwork.getToken(), callback, chatId, apiProvider, messageProcessor);
        }

        return null;
    }

    public static ChatLoaderBase generateChatLoaderVK(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId,
            final APIProvider apiProvider,
            final MessageProcessorBase messageProcessor)
    {
        if (!(apiProvider instanceof VKAPIProvider) ||
            !(messageProcessor instanceof MessageProcessorVK))
        {
            return null;
        }

        UserLoaderSyncVK userLoader =
                (UserLoaderSyncVK) UserLoaderSyncFactory.generateUserLoaderVK(token);

        if (userLoader == null)
            return null;

        VKAPIChat vkAPIChat = ((VKAPIProvider) apiProvider).generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return (ChatLoaderBase)(new ChatLoaderVK(
                token,
                callback,
                chatId,
                (MessageProcessorVK) messageProcessor,
                userLoader,
                vkAPIChat));
    }
}
