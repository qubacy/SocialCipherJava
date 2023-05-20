package com.mcdead.busycoder.socialcipher.client.processor.chat.message.processor;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class MessageProcessorFactory {
    public static MessageProcessorBase generateMessageProcessor() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        AttachmentTypeDefinerInterface attachmentTypeDefiner = AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateMessageProcessorVK(attachmentTypeDefiner, settingsNetwork.getToken());
        }

        return null;
    }

    public static MessageProcessorBase generateMessageProcessorVK(
            final AttachmentTypeDefinerInterface attachmentTypeDefiner,
            final String token)
    {
        VKAPIProvider vkAPIProvider = new VKAPIProvider();
        VKAPIChat vkAPIChat = vkAPIProvider.generateChatAPI();

        if (vkAPIChat == null)
            return null;

        VKAPIAttachment vkAPIAttachment = vkAPIProvider.generateAttachmentAPI();

        if (vkAPIAttachment == null)
            return null;

        return (MessageProcessorBase)(new MessageProcessorVK(
                attachmentTypeDefiner, token, vkAPIChat, vkAPIAttachment));
    }
}
