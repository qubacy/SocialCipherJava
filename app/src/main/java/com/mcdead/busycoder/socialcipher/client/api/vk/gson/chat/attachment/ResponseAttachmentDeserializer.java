package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment;

import static com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentLinked.C_URL_PROP_NAME;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.size.AttachmentSize;

import java.lang.reflect.Type;
import java.util.HashMap;

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

        HashMap<AttachmentSize, String> attachmentSizeUrlHashMap = new HashMap<>();

        attachmentSizeUrlHashMap.put(AttachmentSize.STANDARD, attachmentUrl);

        return new ResponseAttachmentDoc(
                attachmentType.getType(),
                attachmentId,
                attachmentOwnerId,
                attachmentSizeUrlHashMap,
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

        JsonArray sizeArray = attachmentObj.get(C_SIZES_PROP_NAME).getAsJsonArray();
        int sizesCount = sizeArray.size();

        if (sizesCount == 0) return null;

        JsonElement smallPhotoSize = sizeArray.get(0);
        JsonElement standardPhotoSize = sizeArray.get(sizesCount - 1);

        if (!smallPhotoSize.isJsonObject()
         || !standardPhotoSize.isJsonObject())
        {
            return null;
        }

        JsonObject smallPhotoSizeObj = smallPhotoSize.getAsJsonObject();
        JsonObject standardPhotoSizeObj = standardPhotoSize.getAsJsonObject();

        if (smallPhotoSizeObj.get(C_URL_PROP_NAME) == null
         || standardPhotoSizeObj.get(C_URL_PROP_NAME) == null)
        {
            return null;
        }

        String smallPhotoUrl = smallPhotoSizeObj.get(C_URL_PROP_NAME).getAsString();
        String standardPhotoUrl = standardPhotoSizeObj.get(C_URL_PROP_NAME).getAsString();

        HashMap<AttachmentSize, String> attachmentSizeUrlHashMap = new HashMap<>();

        attachmentSizeUrlHashMap.put(AttachmentSize.SMALL, smallPhotoUrl);
        attachmentSizeUrlHashMap.put(AttachmentSize.STANDARD, standardPhotoUrl);

        return new ResponseAttachmentLinked(
                attachmentType.getType(),
                attachmentId,
                attachmentOwnerId,
                attachmentAccessKey,
                attachmentSizeUrlHashMap);
    }
}
