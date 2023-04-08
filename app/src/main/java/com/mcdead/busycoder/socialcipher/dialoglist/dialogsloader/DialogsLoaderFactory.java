package com.mcdead.busycoder.socialcipher.dialoglist.dialogsloader;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerFactory;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerInterface;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

public class DialogsLoaderFactory {
    public static DialogsLoaderBase generateDialogsLoader(
            final DialogsLoadingCallback callback)
    {
        if (callback == null) return null;

        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null) return null;
        if (settingsNetwork.getAPIType() == null) return null;

        DialogTypeDefinerInterface dialogTypeDefiner = DialogTypeDefinerFactory.generateDialogTypeDefiner();

        if (dialogTypeDefiner == null) return null;

        switch (settingsNetwork.getAPIType()) {
            case VK: return new DialogsLoaderVK(settingsNetwork.getToken(), (DialogTypeDefinerVK) dialogTypeDefiner, callback);
        }

        return null;
    }
}
