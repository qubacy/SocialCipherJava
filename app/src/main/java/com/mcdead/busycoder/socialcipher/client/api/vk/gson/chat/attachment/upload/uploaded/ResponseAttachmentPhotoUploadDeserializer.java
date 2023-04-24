package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ResponseAttachmentPhotoUploadDeserializer
    implements JsonDeserializer<ResponseAttachmentPhotoUploaded>
{
    private static final String C_SERVER_PROP_NAME = "server";
    private static final String C_PHOTO_PROP_NAME = "photo";
    private static final String C_HASH_PROP_NAME = "hash";

    @Override
    public ResponseAttachmentPhotoUploaded deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException
    {
        JsonObject rootObject = json.getAsJsonObject();
        ResponseAttachmentPhotoUploaded response = new ResponseAttachmentPhotoUploaded();

        if (!retrievePhotoUploadedData(rootObject, response))
            return null;

        return response;
    }

    private boolean retrievePhotoUploadedData(
            final JsonObject rootObject,
            ResponseAttachmentPhotoUploaded responseAttachmentPhotoUploaded)
    {
        if (rootObject.get(C_SERVER_PROP_NAME) == null
         || rootObject.get(C_PHOTO_PROP_NAME) == null
         || rootObject.get(C_HASH_PROP_NAME) == null)
        {
            return false;
        }
        if (!rootObject.get(C_SERVER_PROP_NAME).isJsonPrimitive()
         || !rootObject.get(C_HASH_PROP_NAME).isJsonPrimitive())
        {
            return false;
        }

        responseAttachmentPhotoUploaded.server = rootObject.get(C_SERVER_PROP_NAME).getAsInt();
        responseAttachmentPhotoUploaded.photo = rootObject.get(C_PHOTO_PROP_NAME).getAsString();
        responseAttachmentPhotoUploaded.hash = rootObject.get(C_HASH_PROP_NAME).getAsString();

        return true;
    }
}
