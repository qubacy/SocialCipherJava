package com.mcdead.busycoder.socialcipher.api.vk.gson.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mcdead.busycoder.socialcipher.api.vk.VKAttachmentType;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.data.UsersStore;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogType;
import com.mcdead.busycoder.socialcipher.data.dialogtype.DialogTypeDefinerVK;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UpdateDeserializer implements JsonDeserializer<ResponseUpdateBody> {
    private static final int C_NEW_MESSAGE_UPDATE_TYPE = 4;
    private static final int C_NEW_MESSAGE_BASIC_FIELDS_COUNT = 7;

    private static final int C_OUTCOMING_MESSAGE_FLAG = 0b10;

    @Override
    public ResponseUpdateBody deserialize(JsonElement json,
                                          Type typeOfT,
                                          JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject rootObj = json.getAsJsonObject();
        ResponseUpdateBody responseUpdateBody = new ResponseUpdateBody();

        responseUpdateBody.ts = rootObj.get("ts").getAsLong();
        JsonArray updateItemArray = rootObj.getAsJsonArray("updates");
        List<ResponseUpdateItem> updateItems = new ArrayList<>();

        for (final JsonElement rawUpdate : updateItemArray) {
            if (!rawUpdate.isJsonArray()) return null;

            JsonArray rawUpdateAsArray = rawUpdate.getAsJsonArray();

            int eventType = rawUpdateAsArray.get(0).getAsInt();

            ResponseUpdateItem updateItem = deserializeUpdateItemByType(eventType, rawUpdateAsArray);

            updateItems.add(updateItem);
        }

        responseUpdateBody.updates = updateItems;

        return responseUpdateBody;
    }

    private ResponseUpdateItem deserializeUpdateItemByType(final int type,
                                                           final JsonArray rawUpdateAsArray)
    {
        switch (type) {
            case C_NEW_MESSAGE_UPDATE_TYPE: return deserializeNewMessageUpdateItem(rawUpdateAsArray);
        }

        return null;
    }

    private ResponseUpdateItem deserializeNewMessageUpdateItem(final JsonArray rawUpdateAsArray) {
        if (rawUpdateAsArray.size() < C_NEW_MESSAGE_BASIC_FIELDS_COUNT)
            return null;

        ResponseUpdateItem responseUpdateItem = new ResponseUpdateItem();

        responseUpdateItem.eventType = C_NEW_MESSAGE_UPDATE_TYPE;
        responseUpdateItem.messageId = rawUpdateAsArray.get(1).getAsLong();
        responseUpdateItem.flags = rawUpdateAsArray.get(2).getAsInt();
        responseUpdateItem.chatId = rawUpdateAsArray.get(3).getAsLong();
        responseUpdateItem.timestamp = rawUpdateAsArray.get(4).getAsLong();
        responseUpdateItem.text = rawUpdateAsArray.get(5).getAsString();

        long localUserPeerId = getLocalUserPeerId();

        if (localUserPeerId == 0) return null;

        DialogType dialogType = (new DialogTypeDefinerVK()).getDialogTypeByPeerId(responseUpdateItem.chatId);

        switch (dialogType) {
            case GROUP:
            case USER: {
                JsonObject titleObj = rawUpdateAsArray.get(6).getAsJsonObject();

                if (responseUpdateItem.chatId == localUserPeerId) {
                    responseUpdateItem.fromPeerId = localUserPeerId;

                } else {
                    if ((responseUpdateItem.flags & C_OUTCOMING_MESSAGE_FLAG) != 0)
                        responseUpdateItem.fromPeerId = localUserPeerId;
                    else
                        responseUpdateItem.fromPeerId = responseUpdateItem.chatId;
                }

                // what to do with a title?

                break;
            }
            case CONVERSATION: {
                JsonObject fromObj = rawUpdateAsArray.get(6).getAsJsonObject();

                responseUpdateItem.fromPeerId = fromObj.get("from").getAsLong();

                break;
            }
            default: return null;
        }

        JsonObject attachmentsObj = rawUpdateAsArray.get(7).getAsJsonObject();
        responseUpdateItem.attachments = deserializeAttachmentList(attachmentsObj);

        return responseUpdateItem;
    }

    private List<ResponseAttachmentBase> deserializeAttachmentList(final JsonObject attachmentsObj) {
        if (attachmentsObj == null) return null;
        if (attachmentsObj.size() <= 0) return null;

        List<ResponseAttachmentBase> attachmentList = new ArrayList<>();
        int curAttachmentIndex = 1;

        while (true) {
            String attachPropName = "attach" + String.valueOf(curAttachmentIndex);
            String attachTypePropName = attachPropName + "_type";

            if (attachmentsObj.get(attachTypePropName) == null)
                break;
            if (attachmentsObj.get(attachPropName) == null)
                break;

            VKAttachmentType attachType = VKAttachmentType
                    .getTypeByString(attachmentsObj.get(attachTypePropName).getAsString());
            ResponseAttachmentBase attachment = null;

            switch (attachType) {
                case PHOTO: attachment = deserializePhotoAttachment(attachPropName, attachmentsObj); break;
                case DOC: attachment = deserializeDocAttachment(attachPropName, attachmentsObj); break;
            }

            ++curAttachmentIndex;

            if (attachment == null) continue;

            attachmentList.add(attachment);
        }

        return attachmentList;
    }

    private ResponseAttachmentBase deserializePhotoAttachment(String attachPropName,
                                                              JsonObject attachmentsObj)
    {
        String attachmentId = attachmentsObj.get(attachPropName).getAsString();

        return new ResponseAttachmentStored(VKAttachmentType.PHOTO.getType(), attachmentId);
    }

    private ResponseAttachmentBase deserializeDocAttachment(String attachPropName,
                                                            JsonObject attachmentsObj)
    {
        String attachmentId = attachmentsObj.get(attachPropName).getAsString();

        return new ResponseAttachmentStored(VKAttachmentType.DOC.getType(), attachmentId);
    }

    private long getLocalUserPeerId() {
        UsersStore usersStore = UsersStore.getInstance();

        if (usersStore == null) return 0;
        if (usersStore.getLocalUser() == null) return 0;

        return usersStore.getLocalUser().getPeerId();
    }
}
