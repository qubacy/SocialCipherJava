package com.mcdead.busycoder.socialcipher.client.data.entity.user.id;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class UserIdCheckerGenerator {
    public static UserIdChecker generateUserIdChecker() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null)
            return null;
        if (settingsNetwork.getAPIType() == null)
            return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new UserIdCheckerVK();
        }

        return null;
    }
}
