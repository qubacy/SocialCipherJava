package com.mcdead.busycoder.socialcipher.data.dialogtype;

import com.mcdead.busycoder.socialcipher.api.common.gson.dialogs.ResponseDialogsItemInterface;

public interface DialogTypeDefinerInterface {
    public DialogType getDialogType(final ResponseDialogsItemInterface dialogItem);
}
