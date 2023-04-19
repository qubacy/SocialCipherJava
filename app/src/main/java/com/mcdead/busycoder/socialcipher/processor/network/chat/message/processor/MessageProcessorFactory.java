package com.mcdead.busycoder.socialcipher.processor.chat.message.processor;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class MessageProcessorFactory {
    public static MessageProcessorBase generateMessageProcessor() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        AttachmentTypeDefinerInterface attachmentTypeDefiner = AttachmentTypeDefinerFactory.generateAttachmentTypeDefiner();

        switch (settingsNetwork.getAPIType()) {
            case VK: return new MessageProcessorVK(attachmentTypeDefiner, settingsNetwork.getToken());
        }

        return null;
    }
}
