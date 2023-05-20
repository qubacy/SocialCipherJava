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
        if (callback == null) return null;

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
                    settingsNetwork.getToken(), callback, messageProcessor, apiProvider);
        }

        return null;
    }

    public static ChatListLoaderBase generateChatListLoaderVK(
            final String token,
            final ChatListLoadingCallback callback,
            final MessageProcessorBase messageProcessor,
            final APIProvider apiProvider)
    {
        if (!(messageProcessor instanceof MessageProcessorVK) ||
            !(apiProvider instanceof VKAPIProvider))
        {
            return null;
        }

        ChatTypeDefinerVK chatTypeDefiner = ChatTypeDefinerFactory.generateDialogTypeDefinerVK();

        if (chatTypeDefiner == null)
            return null;

        UserLoaderSyncVK userLoader = (UserLoaderSyncVK)UserLoaderSyncFactory.generateUserLoaderVK(token);

        if (userLoader == null)
            return null;

        VKAPIChat vkAPIChat = ((VKAPIProvider)apiProvider).generateChatAPI();

        return (ChatListLoaderBase)(new ChatListLoaderVK(
                token, chatTypeDefiner, callback, userLoader, (MessageProcessorVK)messageProcessor, vkAPIChat));
    }
}
