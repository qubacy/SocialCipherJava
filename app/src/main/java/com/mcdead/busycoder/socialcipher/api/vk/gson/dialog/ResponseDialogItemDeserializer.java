package com.mcdead.busycoder.socialcipher.api.vk.gson.dialog;

import static com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked.C_URL_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_ATTACHMENTS_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_FROM_ID_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_ID_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_PEER_ID_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_TEXT_PROP_NAME;
import static com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem.C_TIMESTAMP_PROP_NAME;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mcdead.busycoder.socialcipher.api.vk.VKAttachmentType;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentDoc;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentLinked;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResponseDialogItemDeserializer implements JsonDeserializer<ResponseDialogItem> {
    public static final String C_ATTACHMENT_TYPE_PROP_NAME = "type";
    public static final String C_ATTACHMENT_ID_PROP_NAME = "id";
    public static final String C_ATTACHMENT_OWNER_ID_PROP_NAME = "owner_id";

    public static final String C_SIZES_PROP_NAME = "sizes";

    @Override
    public ResponseDialogItem deserialize(JsonElement json,
                                          Type typeOfT,
                                          JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject rootObj = json.getAsJsonObject();
        ResponseDialogItem responseDialogItem = new ResponseDialogItem();

        if (!initMainPart(rootObj, responseDialogItem)) return null;
        if (!rootObj.get(C_ATTACHMENTS_PROP_NAME).isJsonArray()) return null;
        if (!initAttachmentPart(rootObj.get(C_ATTACHMENTS_PROP_NAME).getAsJsonArray(), responseDialogItem))
            return null;

        return responseDialogItem;
    }

    private boolean initMainPart(JsonObject rootObj,
                                 ResponseDialogItem responseDialogItem)
    {
        if (responseDialogItem == null) return false;

        responseDialogItem.id = rootObj.get(C_ID_PROP_NAME).getAsLong();
        responseDialogItem.fromId = rootObj.get(C_FROM_ID_PROP_NAME).getAsLong();
        responseDialogItem.peerId = rootObj.get(C_PEER_ID_PROP_NAME).getAsLong();
        responseDialogItem.timestamp = rootObj.get(C_TIMESTAMP_PROP_NAME).getAsLong();
        responseDialogItem.text = rootObj.get(C_TEXT_PROP_NAME).getAsString();

        return true;
    }

    private boolean initAttachmentPart(JsonArray attachmentsJson,
                                       ResponseDialogItem responseDialogItem)
    {
        if (attachmentsJson.size() == 0) return true;
        if (responseDialogItem == null) return false;

        List<ResponseAttachmentBase> attachments = new ArrayList<>();

        for (final JsonElement elem : attachmentsJson) {
            if (!elem.isJsonObject()) return false;

            ResponseAttachmentBase attachment = processAttachment(elem.getAsJsonObject());

            if (attachment != null)
                attachments.add(attachment);
        }

        responseDialogItem.attachments = attachments;

        return true;
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
        if (!attachmentObj.has(C_ATTACHMENT_ID_PROP_NAME)
         || !attachmentObj.has(C_ATTACHMENT_OWNER_ID_PROP_NAME))
        {
            return null;
        }

        long attachmentId = attachmentObj.get(C_ATTACHMENT_ID_PROP_NAME).getAsLong();
        long attachmentOwnerId = attachmentObj.get(C_ATTACHMENT_OWNER_ID_PROP_NAME).getAsLong();

        switch (attachmentType) {
            case DOC: return processLinkedDocAttachment(attachmentType, attachmentId, attachmentOwnerId, attachmentObj);
            case PHOTO: return processLinkedPhotoAttachment(attachmentType, attachmentId, attachmentOwnerId, attachmentObj);
        }

        return null;
    }

    private ResponseAttachmentBase processLinkedDocAttachment(VKAttachmentType attachmentType,
                                                              long attachmentId,
                                                              long attachmentOwnerId,
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
                attachmentType.getType(), attachmentId,
                attachmentOwnerId, photoUrl);
    }

//    private ResponseAttachmentBase processStoredAttachment(VKAttachmentType attachmentType,
//                                                           JsonObject attachmentObj)
//    {
//        JsonObject attachmentData = attachmentObj.get(attachmentType.getType()).getAsJsonObject();
//
//        long attachmentId = attachmentData.get(C_ATTACHMENT_ID_PROP_NAME).getAsLong();
//        long attachmentOwnerId = attachmentData.get(C_ATTACHMENT_OWNER_ID_PROP_NAME).getAsLong();
//
//        return new ResponseAttachmentStored(attachmentType.getType(), attachmentId, attachmentOwnerId);
//    }
}
