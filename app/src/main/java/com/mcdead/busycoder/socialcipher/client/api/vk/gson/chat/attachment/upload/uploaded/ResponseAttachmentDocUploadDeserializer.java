package com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ResponseAttachmentDocUploadDeserializer
    implements JsonDeserializer<ResponseAttachmentDocUploaded>
{
    private static final String C_FILE_PROP_NAME = "file";

    @Override
    public ResponseAttachmentDocUploaded deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException
    {
        JsonObject rootObject = json.getAsJsonObject();
        ResponseAttachmentDocUploaded response = new ResponseAttachmentDocUploaded();

        if (!retrieveDocUploadedData(rootObject, response))
            return null;

        return response;
    }

    private boolean retrieveDocUploadedData(
            final JsonObject rootObject,
            ResponseAttachmentDocUploaded responseAttachmentDocUploaded)
    {
        if (rootObject.get(C_FILE_PROP_NAME) == null)
            return false;
        if (!rootObject.get(C_FILE_PROP_NAME).isJsonPrimitive())
            return false;

        responseAttachmentDocUploaded.file = rootObject.get(C_FILE_PROP_NAME).getAsString();

        return true;
    }
}
