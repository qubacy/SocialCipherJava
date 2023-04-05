package com.mcdead.busycoder.socialcipher.data.dialogtype;

import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class DialogTypeDefinerFactory {
    public static DialogTypeDefinerInterface generateDialogTypeDefiner() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return generateDialogTypeDefinerVK();
        }

        return null;
    }

    private static DialogTypeDefinerVK generateDialogTypeDefinerVK() {
        return new DialogTypeDefinerVK();
    }
}
