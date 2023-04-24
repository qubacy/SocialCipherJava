package com.mcdead.busycoder.socialcipher.client.api.vk.gson.update;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.update.ResponseUpdateItemInterface;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;

import java.io.Serializable;
import java.util.List;

public class ResponseUpdateItem implements ResponseUpdateItemInterface, Serializable {
    public int eventType;
    public long messageId;
    public int flags;
    public long chatId;
    public long timestamp;
    public String text;
    public long fromPeerId;
    public List<ResponseAttachmentBase> attachments;


//    public boolean isLocal() {
//        return (flags & C_IS_LOCAL_FLAG_VALUE) > 0;
//    }


}
