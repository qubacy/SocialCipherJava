package com.mcdead.busycoder.socialcipher.data.entity.dialog;

import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;

public class DialogEntityGroup extends DialogEntity {

    public DialogEntityGroup(long peerId) {
        super(peerId, DialogType.GROUP);
    }
}
