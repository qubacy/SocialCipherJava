package com.mcdead.busycoder.socialcipher.data.entity.dialog;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;

public class DialogEntityUser extends DialogEntity {

    public DialogEntityUser(long peerId) {
        super(peerId, DialogType.USER);
    }
}
