package com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class AttachmentTypeDefinerFactory {
    public static AttachmentTypeDefinerInterface generateAttachmentTypeDefiner() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new AttachmentTypeDefinerVK();
        }

        return null;
    }
}
