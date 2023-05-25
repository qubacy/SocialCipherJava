package com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentBaseUploadServerBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentDocUploadServerBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentDocUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentPhotoUploadServerBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentPhotoUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentDocSaveWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentPhotoSaveBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentPhotoSaveWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded.ResponseAttachmentDocUploadDeserializer;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded.ResponseAttachmentDocUploaded;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded.ResponseAttachmentPhotoUploadDeserializer;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded.ResponseAttachmentPhotoUploaded;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.uploaded.ResponseAttachmentUploaded;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIUploadAttachment;
import com.mcdead.busycoder.socialcipher.client.data.entity.attachment.type.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.chat.id.ChatIdCheckerVK;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResult;
import com.mcdead.busycoder.socialcipher.client.processor.chat.attachment.uploader.result.AttachmentUploadedResultVK;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AttachmentUploaderSyncVK extends AttachmentUploaderSyncBase {
    private static final int C_TIMEOUT_SEC_VALUE = 30;

    private static final int C_MAX_ATTACHMENT_COUNT = 8;
    private static final long C_MAX_ATTACHMENT_SIZE_BYTES = 209715200;

    final protected VKAPIUploadAttachment m_vkAPIUploadAttachment;

    protected AttachmentUploaderSyncVK(
            final String token,
            final long chatId,
            final ContentResolver contentResolver,
            final VKAPIUploadAttachment vkAPIUploadAttachment)
    {
        super(token, chatId, contentResolver);

        m_vkAPIUploadAttachment = vkAPIUploadAttachment;
    }

    public static AttachmentUploaderSyncVK getInstance(
            final String token,
            final long chatId,
            final ContentResolver contentResolver,
            final VKAPIUploadAttachment vkAPIUploadAttachment)
    {
        if (token == null || contentResolver == null || vkAPIUploadAttachment == null)
            return null;

        ChatIdCheckerVK chatIdCheckerVK = new ChatIdCheckerVK();

        if (!chatIdCheckerVK.isValid(chatId) || token.isEmpty()) return null;

        return new AttachmentUploaderSyncVK(token, chatId, contentResolver, vkAPIUploadAttachment);
    }

    private Error getUploadingUrlForPhoto(
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        Response<ResponseAttachmentPhotoUploadServerWrapper> response =
                m_vkAPIUploadAttachment.getPhotoUploadServer(m_token, m_peerId).execute();

        if (!response.isSuccessful())
            return new Error(
                    "Request for a photo uploading server link has been failed!",
                    true);
        if (response.body().error != null)
            return new Error(response.body().error.message, true);

        resultUploadingServerDataWrapper.setValue(response.body().response);

        return null;
    }

    private Error getUploadingUrlForDoc(
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        Response<ResponseAttachmentDocUploadServerWrapper> response =
                m_vkAPIUploadAttachment.getDocUploadServer(m_token, m_peerId).execute();

        if (!response.isSuccessful())
            return new Error(
                    "Request for a doc uploading server link has been failed!",
                    true);
        if (response.body().error != null)
            return new Error(response.body().error.message, true);

        resultUploadingServerDataWrapper.setValue(response.body().response);

        return null;
    }

    private Error getAttachmentUploadingUrl(
            final AttachmentData attachmentData,
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        switch (attachmentData.getType()) {
            case IMAGE: return getUploadingUrlForPhoto(resultUploadingServerDataWrapper);
            case DOC:   return getUploadingUrlForDoc(resultUploadingServerDataWrapper);
        }

        return new Error(
                "Cannot get an uploading link for a provided attachment!",
                true);
    }

    private Error getFileBytesByUri(
            final Uri fileUri,
            ObjectWrapper<byte[]> fileBytesWrapper)
    {
        try (InputStream in = m_contentResolver.openInputStream(fileUri)) {
            long availableBytes = in.available();

            if (availableBytes > C_MAX_ATTACHMENT_SIZE_BYTES) {
                in.close();

                return new Error(
                        "Provided attachment file was too large!",
                        false
                );
            }

            byte[] fileBytes = new byte[in.available()];

            in.read(fileBytes);

            fileBytesWrapper.setValue(fileBytes);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    private Request generateDefaultSendAttachmentRequest(
            final String contentFieldName,
            final AttachmentData attachmentData,
            final String uploadingUrl,
            final byte[] attachmentFileBytes)
    {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        contentFieldName,
                        attachmentData.getFileName(),
                        RequestBody.create(
                                MediaType.parse(attachmentData.getMimeType()),
                                attachmentFileBytes)
                )
                .build();
        Request uploadingUrlRequest = new Request.Builder()
                .url(uploadingUrl)
                .post(requestBody)
                .build();

        return uploadingUrlRequest;
    }

    private Error sendPhotoAttachmentToServer(
            final OkHttpClient httpClient,
            final AttachmentData attachmentData,
            final byte[] attachmentFileBytes,
            final ResponseAttachmentBaseUploadServerBody attachmentUploadingServerData,
            ObjectWrapper<ResponseAttachmentUploaded> resultResponseAttachmentUploadedWrapper)
            throws IOException
    {
        Request uploadingUrlRequest =
                generateDefaultSendAttachmentRequest(
                        "photo",
                        attachmentData,
                        attachmentUploadingServerData.uploadUrl,
                        attachmentFileBytes);

        okhttp3.Response response = httpClient.newCall(uploadingUrlRequest).execute();

        if (!response.isSuccessful())
            return new Error("Obtained uploading result wasn't successful!", true);

        String responseJsonBody = response.body().string();

        if (responseJsonBody.isEmpty())
            return new Error("Obtained uploading result was empty!", true);

        Gson gson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentPhotoUploaded.class,
                new ResponseAttachmentPhotoUploadDeserializer()).create();

        ResponseAttachmentPhotoUploaded responseAttachmentPhotoUploaded =
                gson.fromJson(responseJsonBody, ResponseAttachmentPhotoUploaded.class);

        if (responseAttachmentPhotoUploaded == null)
            return new Error("Obtained uploading result wasn't successful!", true);

        resultResponseAttachmentUploadedWrapper.setValue(responseAttachmentPhotoUploaded);

        return null;
    }

    private Error sendDocAttachmentToServer(
            final OkHttpClient httpClient,
            final AttachmentData attachmentData,
            final byte[] attachmentFileBytes,
            final ResponseAttachmentBaseUploadServerBody attachmentUploadingServerData,
            ObjectWrapper<ResponseAttachmentUploaded> resultResponseAttachmentUploadedWrapper)
            throws IOException
    {
        Request uploadingUrlRequest =
                generateDefaultSendAttachmentRequest(
                        "file",
                        attachmentData,
                        attachmentUploadingServerData.uploadUrl,
                        attachmentFileBytes);

        okhttp3.Response response = httpClient.newCall(uploadingUrlRequest).execute();

        if (!response.isSuccessful())
            return new Error("Obtained uploading result wasn't successful!", false);

        String responseJsonBody = response.body().string();

        if (responseJsonBody.isEmpty())
            return new Error("Obtained uploading result was empty!", true);

        Gson gson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentDocUploaded.class,
                new ResponseAttachmentDocUploadDeserializer()).create();

        ResponseAttachmentDocUploaded responseAttachmentDocUploaded =
                gson.fromJson(responseJsonBody, ResponseAttachmentDocUploaded.class);

        if (responseAttachmentDocUploaded == null)
            return new Error("Obtained uploading result wasn't successful!", true);

        resultResponseAttachmentUploadedWrapper.setValue(responseAttachmentDocUploaded);

        return null;
    }

    private Error sendAttachmentToServer(
            final OkHttpClient httpClient,
            final AttachmentData attachmentData,
            final ResponseAttachmentBaseUploadServerBody attachmentUploadingServerData,
            ObjectWrapper<ResponseAttachmentUploaded> resultResponseAttachmentUploadedWrapper)
            throws IOException
    {
        ObjectWrapper<byte[]> fileBytesWrapper = new ObjectWrapper<>();
        Error readFileBytesError = getFileBytesByUri(attachmentData.getUri(), fileBytesWrapper);

        if (readFileBytesError != null)
            return readFileBytesError;

        switch (attachmentData.getType()) {
            case IMAGE: return sendPhotoAttachmentToServer(
                    httpClient,
                    attachmentData,
                    fileBytesWrapper.getValue(),
                    attachmentUploadingServerData,
                    resultResponseAttachmentUploadedWrapper);
            case DOC: return sendDocAttachmentToServer(
                    httpClient,
                    attachmentData,
                    fileBytesWrapper.getValue(),
                    attachmentUploadingServerData,
                    resultResponseAttachmentUploadedWrapper);
        }

        return new Error(
                "Cannot upload content of a provided attachment!",
                true);
    }

    private Error saveSentPhotoAttachment(
            final ResponseAttachmentPhotoUploadServerBody responseAttachmentPhotoUploadServerBody,
            final ResponseAttachmentPhotoUploaded responseAttachmentPhotoUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        Response<ResponseAttachmentPhotoSaveWrapper> response =
                m_vkAPIUploadAttachment.saveUploadedPhoto(
                        m_token,
                        responseAttachmentPhotoUploadServerBody.albumId,
                        responseAttachmentPhotoUploaded.photo,
                        responseAttachmentPhotoUploaded.server,
                        responseAttachmentPhotoUploaded.hash).execute();

        if (!response.isSuccessful())
            return new Error("Photo Attachment saving operation has been failed!", true);
        if (response.body().error != null)
            return new Error(response.body().error.message, true);
        if (response.body().response.isEmpty())
            return new Error("Photo Attachment saving operation has been failed!", true);

        ResponseAttachmentPhotoSaveBody responseAttachmentPhotoSaveBody =
                response.body().response.get(0);

        if (responseAttachmentPhotoSaveBody == null)
            return new Error("Photo Attachment saving operation response was null!", true);

        long photoId = responseAttachmentPhotoSaveBody.id;
        long ownerId = responseAttachmentPhotoSaveBody.ownerId;
        String accessKey = responseAttachmentPhotoSaveBody.accessKey;

        ResponseAttachmentStored attachmentStored = null;

        if (accessKey == null) {
            attachmentStored =
                    new ResponseAttachmentStored(
                            AttachmentTypeDefinerVK.C_IMAGE_TYPE_NAME,
                            photoId,
                            ownerId);
        } else {
            attachmentStored =
                    new ResponseAttachmentStored(
                            AttachmentTypeDefinerVK.C_IMAGE_TYPE_NAME,
                            photoId,
                            ownerId,
                            accessKey);
        }

        attachmentIdWrapper.setValue(attachmentStored.getTypedFullAttachmentID());

        return null;
    }

    private Error saveSentDocAttachment(
            final ResponseAttachmentDocUploadServerBody responseAttachmentDocUploadServerBody,
            final ResponseAttachmentDocUploaded responseAttachmentDocUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        Response<ResponseAttachmentDocSaveWrapper> response =
                m_vkAPIUploadAttachment.saveUploadedDoc(
                        m_token,
                        responseAttachmentDocUploaded.file).execute();

        if (!response.isSuccessful())
            return new Error("Doc Attachment saving operation has been failed!", true);
        if (response.body().error != null)
            return new Error(response.body().error.message, true);
        if (response.body().response == null)
            return new Error("Doc Attachment saving operation has been failed!", true);

        ResponseAttachmentStored responseAttachmentDoc =
                response.body().response.attachmentStored;

        if (responseAttachmentDoc == null)
            return new Error("Doc Attachment saving operation response was null!", true);

        attachmentIdWrapper.setValue(responseAttachmentDoc.getTypedFullAttachmentID());

        return null;
    }

    private Error saveSentAttachment(
            final AttachmentData attachmentData,
            final ResponseAttachmentBaseUploadServerBody responseAttachmentUploadServerBody,
            final ResponseAttachmentUploaded responseAttachmentUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        switch (attachmentData.getType()) {
            case IMAGE: return saveSentPhotoAttachment(
                    (ResponseAttachmentPhotoUploadServerBody) responseAttachmentUploadServerBody,
                    (ResponseAttachmentPhotoUploaded) responseAttachmentUploaded,
                    attachmentIdWrapper);
            case DOC: return saveSentDocAttachment(
                    (ResponseAttachmentDocUploadServerBody) responseAttachmentUploadServerBody,
                    (ResponseAttachmentDocUploaded) responseAttachmentUploaded,
                    attachmentIdWrapper);
        }

        return new Error(
                "Cannot save content of a provided attachment!",
                true);
    }

    public Error uploadAttachments(
            final List<AttachmentData> uploadingAttachmentList,
            ObjectWrapper<AttachmentUploadedResult> resultAttachmentListStringWrapper)
    {
        if (uploadingAttachmentList == null)
            return null;
        if (uploadingAttachmentList.isEmpty())
            return null;

        StringBuilder resultAttachmentListString = new StringBuilder();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .writeTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .readTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .build();

        int attachmentCount = uploadingAttachmentList.size();

        try {
            for (int i = 0; i < attachmentCount; ++i) {
                final AttachmentData uploadingAttachmentData = uploadingAttachmentList.get(i);
                Error error = null;

                ObjectWrapper<ResponseAttachmentBaseUploadServerBody> responseUploadServerWrapper =
                        new ObjectWrapper<>();

                error = getAttachmentUploadingUrl(
                        uploadingAttachmentData,
                        responseUploadServerWrapper);

                if (error != null) return error;

                ObjectWrapper<ResponseAttachmentUploaded> responseUploadedWrapper =
                        new ObjectWrapper<>();

                error = sendAttachmentToServer(
                        httpClient,
                        uploadingAttachmentData,
                        responseUploadServerWrapper.getValue(),
                        responseUploadedWrapper);

                if (error != null) return error;

                ObjectWrapper<String> attachmentIdWrapper =
                        new ObjectWrapper<>();

                error = saveSentAttachment(
                        uploadingAttachmentData,
                        responseUploadServerWrapper.getValue(),
                        responseUploadedWrapper.getValue(),
                        attachmentIdWrapper);

                if (error != null) return error;

                resultAttachmentListString.append(
                        i + 1 == attachmentCount
                                ? attachmentIdWrapper.getValue()
                                : attachmentIdWrapper.getValue() + ',');

                if (i == C_MAX_ATTACHMENT_COUNT) break;
            }

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        resultAttachmentListStringWrapper.setValue(
                new AttachmentUploadedResultVK(resultAttachmentListString.toString()));

        return null;
    }
}
