package com.mcdead.busycoder.socialcipher.api.vk.gson.chat.attachment.upload.save;

import com.google.gson.annotations.SerializedName;

public class ResponseAttachmentPhotoSaveBody {
    public @SerializedName("album_id") long albumId;
    public long id;
    public @SerializedName("owner_id") long ownerId;
    public @SerializedName("access_key") String accessKey;
}
