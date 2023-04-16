package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mcdead.busycoder.socialcipher.api.vk.gson.Error;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.lang.reflect.Type;

public class ResponseAttachmentDocSaveBodyDeserializer
        implements JsonDeserializer<ResponseAttachmentDocSaveBody>
{
    private static final String C_RESPONSE_PROP_NAME = "response";
    private static final String C_ERROR_PROP_NAME = "error";

    private static final String C_ERROR_MESSAGE_PROP_NAME = "error_msg";

    private static final String C_TYPE_PROP_NAME = "type";

    private static final String C_ID_PROP_NAME = "id";
    private static final String C_OWNER_ID_PROP_NAME = "owner_id";

    @Override
    public ResponseAttachmentDocSaveBody deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject rootObj = json.getAsJsonObject();
        ResponseAttachmentDocSaveBody responseBody = new ResponseAttachmentDocSaveBody();

        if (!retrieveResponsePart(rootObj, responseBody))
            return null;

        return responseBody;
    }

    private boolean retrieveResponsePart(
            final JsonObject rootJson,
            ResponseAttachmentDocSaveBody responseBody)
    {
        if (rootJson.get(C_TYPE_PROP_NAME) == null)
            return false;
        if (!rootJson.get(C_TYPE_PROP_NAME).isJsonPrimitive())
            return false;

        String responseAttachmentType = rootJson.get(C_TYPE_PROP_NAME).getAsString();

        if (rootJson.get(responseAttachmentType) == null)
            return false;
        if (!rootJson.get(responseAttachmentType).isJsonObject())
            return false;

        JsonObject responseTypeJson = rootJson.getAsJsonObject(responseAttachmentType);

        if (responseTypeJson.get(C_ID_PROP_NAME) == null
         || responseTypeJson.get(C_OWNER_ID_PROP_NAME) == null)
        {
            return false;
        }

        if (!responseTypeJson.get(C_ID_PROP_NAME).isJsonPrimitive()
         || !responseTypeJson.get(C_OWNER_ID_PROP_NAME).isJsonPrimitive())
        {
            return false;
        }

        long responseAttachmentId = responseTypeJson.get(C_ID_PROP_NAME).getAsLong();
        long responseAttachmentOwnerId = responseTypeJson.get(C_OWNER_ID_PROP_NAME).getAsLong();

        ResponseAttachmentStored responseAttachmentStored =
                new ResponseAttachmentStored(
                        responseAttachmentType,
                        responseAttachmentId,
                        responseAttachmentOwnerId);

        responseBody.attachmentStored = responseAttachmentStored;

        return true;
    }

    private boolean retrieveErrorPart(
            final JsonObject rootJson,
            ObjectWrapper<Error> responseErrorWrapper)
    {
        if (rootJson.get(C_ERROR_PROP_NAME) == null)
            return false;
        if (!rootJson.get(C_ERROR_PROP_NAME).isJsonObject())
            return false;

        JsonObject errorJson = rootJson.getAsJsonObject(C_ERROR_PROP_NAME);

        if (errorJson.get(C_ERROR_MESSAGE_PROP_NAME) == null)
            return false;
        if (!errorJson.get(C_ERROR_MESSAGE_PROP_NAME).isJsonPrimitive())
            return false;

        String responseErrorMessage = errorJson.get(C_ERROR_MESSAGE_PROP_NAME).getAsString();

        Error responseError = new Error(responseErrorMessage);

        responseErrorWrapper.setValue(responseError);

        return true;
    }
}
