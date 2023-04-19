package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.attachment.upload.getserver;

import com.google.gson.annotations.SerializedName;

public class ResponseAttachmentPhotoUploadServerBody extends ResponseAttachmentBaseUploadServerBody
{
    public @SerializedName("album_id") long albumId;
    public @SerializedName("user_id") long userId;
}
