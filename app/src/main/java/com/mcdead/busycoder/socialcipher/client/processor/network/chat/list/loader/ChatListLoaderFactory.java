package com.mcdead.busycoder.socialcipher.client.processor.chat.list.loader;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.type.ChatTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncFactory;
import com.mcdead.busycoder.socialcipher.client.processor.user.loader.UserLoaderSyncVK;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorStore;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorBase;
import com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor.MessageProcessorVK;

public class ChatListLoaderFactory {
    public static ChatListLoaderBase generateChatListLoader(
            final ChatListLoadingCallback callback)
    {
        if (!checkCommonArgsValidity(callback))
            return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        MessageProcessorBase messageProcessor = MessageProcessorStore.getProcessor();

        if (messageProcessor == null)
            return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateChatListLoaderVK(
                    settingsNetwork.getToken(),
                    callback,
                    (MessageProcessorVK) messageProcessor,
                    (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static ChatListLoaderVK generateChatListLoaderVK(
            final String token,
            final ChatListLoadingCallback callback,
            final MessageProcessorVK messageProcessorVK,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(token, callback, messageProcessorVK, vkAPIProvider))
            return null;

        ChatTypeDefinerVK chatTypeDefiner = ChatTypeDefinerFactory.generateDialogTypeDefinerVK();

        if (chatTypeDefiner == null)
            return null;

        UserLoaderSyncVK userLoader =
                (UserLoaderSyncVK)UserLoaderSyncFactory.
                        generateUserLoaderVK(token, vkAPIProvider);

        if (userLoader == null)
            return null;

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        return new ChatListLoaderVK(
                token, chatTypeDefiner, callback, userLoader, messageProcessorVK, vkAPIChat);
    }

    private static boolean checkCommonArgsValidityForImpl(
            final String token,
            final ChatListLoadingCallback callback,
            final MessageProcessorVK messageProcessorVK,
            final VKAPIProvider vkAPIProvider)
    {
        if (!(checkCommonArgsValidity(callback)) || token == null || messageProcessorVK == null ||
            vkAPIProvider == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }

    private static boolean checkCommonArgsValidity(
            final ChatListLoadingCallback callback)
    {
        if (callback == null) return false;

        return true;
    }
}
