package com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor;

import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.APIProviderGenerator;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class MessageProcessorFactory {
    public static MessageProcessorBase generateMessageProcessor() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        AttachmentTypeDefinerInterface attachmentTypeDefiner =
                AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        if (attachmentTypeDefiner == null)
            return null;

        ChatIdChecker chatIdChecker =
                ChatIdCheckerGenerator.generateChatIdChecker();

        if (chatIdChecker == null)
            return null;

        APIProvider apiProvider = APIProviderGenerator.generateAPIProvider();

        if (apiProvider == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return (MessageProcessorBase) generateMessageProcessorVK(
                    (AttachmentTypeDefinerVK) attachmentTypeDefiner,
                    settingsNetwork.getToken(),
                    (ChatIdCheckerVK) chatIdChecker,
                    (VKAPIProvider) apiProvider);
        }

        return null;
    }

    public static MessageProcessorVK generateMessageProcessorVK(
            final AttachmentTypeDefinerVK attachmentTypeDefinerVK,
            final String token,
            final ChatIdCheckerVK chatIdCheckerVK,
            final VKAPIProvider vkAPIProvider)
    {
        if (!checkCommonArgsValidityForImpl(
                attachmentTypeDefinerVK, token, chatIdCheckerVK, vkAPIProvider))
        {
            return null;
        }

        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        VKAPIAttachment vkAPIAttachment = vkAPIProvider.generateAttachmentAPI();

        if (vkAPIAttachment == null)
            return null;

        return new MessageProcessorVK(
                attachmentTypeDefinerVK, token, chatIdCheckerVK, vkAPIChat, vkAPIAttachment);
    }

    public static boolean checkCommonArgsValidityForImpl(
            final AttachmentTypeDefinerInterface attachmentTypeDefiner,
            final String token,
            final ChatIdChecker chatIdChecker,
            final APIProvider apiProvider)
    {
        if (attachmentTypeDefiner == null || token == null ||
            chatIdChecker == null || apiProvider == null)
        {
            return false;
        }
        if (token.isEmpty()) return false;

        return true;
    }
}
