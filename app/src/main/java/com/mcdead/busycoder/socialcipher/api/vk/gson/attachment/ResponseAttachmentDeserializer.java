package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

import static com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked.C_URL_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_ATTACHMENTS_PROP_NAME;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mcdead.busycoder.socialcipher.api.vk.VKAttachmentType;

import java.lang.reflect.Type;

public class ResponseAttachmentDeserializer implements JsonDeserializer<ResponseAttachmentBase> {
    public static final String C_ATTACHMENT_TYPE_PROP_NAME = "type";
    public static final String C_ATTACHMENT_ID_PROP_NAME = "id";
    public static final String C_ATTACHMENT_OWNER_ID_PROP_NAME = "owner_id";
    public static final String C_ATTACHMENT_ACCESS_KEY_PROP_NAME = "access_key";

    public static final String C_SIZES_PROP_NAME = "sizes";

    @Override
    public ResponseAttachmentBase deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject rootObj = json.getAsJsonObject();

        ResponseAttachmentBase responseAttachmentBase =
                processAttachment(rootObj);

        return responseAttachmentBase;
    }

    private ResponseAttachmentBase processAttachment(JsonObject attachmentObj)
    {
        if (attachmentObj == null) return null;

        String attachmentTypeString = attachmentObj.get(C_ATTACHMENT_TYPE_PROP_NAME).getAsString();
        VKAttachmentType attachmentType = VKAttachmentType.getTypeByString(attachmentTypeString);

        if (attachmentType == null) return null;

        if (!attachmentObj.get(attachmentTypeString).isJsonObject())
            return null;

        JsonObject attachmentData = attachmentObj.get(attachmentType.getType()).getAsJsonObject();

        switch (attachmentType) {
            case DOC:
            case PHOTO: return processLinkedDefaultAttachment(attachmentType, attachmentData);
        }

        return null;
    }

    private ResponseAttachmentBase processLinkedDefaultAttachment(
            VKAttachmentType attachmentType,
            JsonObject attachmentObj)
    {
        if (attachmentObj.get(C_ATTACHMENT_ID_PROP_NAME) == null
         || attachmentObj.get(C_ATTACHMENT_OWNER_ID_PROP_NAME) == null)
        {
            return null;
        }

        long attachmentId = attachmentObj.get(C_ATTACHMENT_ID_PROP_NAME).getAsLong();
        long attachmentOwnerId = attachmentObj.get(C_ATTACHMENT_OWNER_ID_PROP_NAME).getAsLong();
        String attachmentAccessKey = null;

        if (attachmentObj.get(C_ATTACHMENT_ACCESS_KEY_PROP_NAME) != null) {
            attachmentAccessKey = attachmentObj.get(C_ATTACHMENT_ACCESS_KEY_PROP_NAME).getAsString();
        }

        switch (attachmentType) {
            case DOC: return processLinkedDocAttachment(
                    attachmentType,
                    attachmentId,
                    attachmentOwnerId,
                    attachmentAccessKey,
                    attachmentObj);
            case PHOTO: return processLinkedPhotoAttachment(
                    attachmentType,
                    attachmentId,
                    attachmentOwnerId,
                    attachmentAccessKey,
                    attachmentObj);
        }

        return null;
    }

    private ResponseAttachmentBase processLinkedDocAttachment(VKAttachmentType attachmentType,
                                                              long attachmentId,
                                                              long attachmentOwnerId,
                                                              String attachmentAccessKey,
                                                              JsonObject attachmentObj)
    {
        if (attachmentObj.get(C_URL_PROP_NAME) == null)
            return null;

        String attachmentUrl = attachmentObj.get(C_URL_PROP_NAME).getAsString();
        String attachmentExt = attachmentObj.get(ResponseAttachmentDoc.C_EXT_PROP_NAME).getAsString();

        return new ResponseAttachmentDoc(
                attachmentType.getType(), attachmentId,
                attachmentOwnerId, attachmentUrl,
                attachmentExt);
    }

    private ResponseAttachmentBase processLinkedPhotoAttachment(VKAttachmentType attachmentType,
                                                                long attachmentId,
                                                                long attachmentOwnerId,
                                                                String attachmentAccessKey,
                                                                JsonObject attachmentObj)
    {
        if (attachmentObj.get(C_SIZES_PROP_NAME) == null)
            return null;
        if (!attachmentObj.get(C_SIZES_PROP_NAME).isJsonArray())
            return null;

        int sizesCount = attachmentObj.get(C_SIZES_PROP_NAME).getAsJsonArray().size();

        if (sizesCount == 0) return null;

        JsonElement biggestPhotoSize = attachmentObj.get(C_SIZES_PROP_NAME).getAsJsonArray().get(sizesCount - 1);

        if (!biggestPhotoSize.isJsonObject()) return null;

        JsonObject biggestPhotoSizeObj = biggestPhotoSize.getAsJsonObject();

        if (biggestPhotoSizeObj.get(C_URL_PROP_NAME) == null)
            return null;

        String photoUrl = biggestPhotoSizeObj.get(C_URL_PROP_NAME).getAsString();

        return new ResponseAttachmentLinked(
                attachmentType.getType(),
                attachmentId,
                attachmentOwnerId,
                attachmentAccessKey,
                photoUrl);
    }
}
