package com.mcdead.busycoder.socialcipher.client.processor.chat.loader;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerVK;
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
        if (!checkCommonArgsValidity(callback))
            return null;

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
                    settingsNetwork.getToken(),
                    callback,
                    chatId,
                    (VKAPIProvider) apiProvider,
                    (MessageProcessorVK) messageProcessor);
        }

        return null;
    }

    public static ChatLoaderVK generateChatLoaderVK(
            final String token,
            final ChatLoadingCallback callback,
            final long chatId,
            final VKAPIProvider vkAPIProvider,
            final MessageProcessorVK messageProcessorVK)
    {
        if (!checkCommonArgsValidityForImpl(token, callback, vkAPIProvider, messageProcessorVK))
            return null;

        ChatIdCheckerVK chatIdCheckerVK = new ChatIdCheckerVK();

        if (!chatIdCheckerVK.isValid(chatId))
            return null;

        UserLoaderSyncVK userLoader =
                (UserLoaderSyncVK) UserLoaderSyncFactory.
                        generateUserLoaderVK(token, vkAPIProvider);

        if (userLoader == null)
            return null;

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        return new ChatLoaderVK(
                token,
                callback,
                chatId,
                messageProcessorVK,
                userLoader,
                vkAPIChat);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final ChatLoadingCallback callback,
            final APIProvider apiProvider,
            final MessageProcessorBase messageProcessor)
    {
        if (!checkCommonArgsValidity(callback) || token == null || apiProvider == null ||
            messageProcessor == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }

    private static boolean checkCommonArgsValidity(
            final ChatLoadingCallback callback)
    {
        if (callback == null) return false;

        return true;
    }
}
