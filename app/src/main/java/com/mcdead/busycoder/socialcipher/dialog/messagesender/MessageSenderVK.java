package com.mcdead.busycoder.socialcipher.dialog.messagesender;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.api.APIStore;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.ResponseAttachmentStored;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.getserver.ResponseAttachmentDocUploadServerBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.getserver.ResponseAttachmentDocUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.getserver.ResponseAttachmentPhotoUploadServerBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.getserver.ResponseAttachmentPhotoUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.getserver.ResponseAttachmentBaseUploadServerBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveBodyDeserializer;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentPhotoSaveBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentPhotoSaveWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.uploaded.ResponseAttachmentDocUploadDeserializer;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.uploaded.ResponseAttachmentDocUploaded;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.uploaded.ResponseAttachmentPhotoUploadDeserializer;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.uploaded.ResponseAttachmentPhotoUploaded;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.uploaded.ResponseAttachmentUploaded;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseSendMessageWrapper;
import com.mcdead.busycoder.socialcipher.attachmentpicker.data.AttachmentData;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentTypeDefinerVK;
import com.mcdead.busycoder.socialcipher.error.Error;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageSenderVK extends MessageSenderBase {
    private static final int C_TIMEOUT_SEC_VALUE = 30;

    private static final int C_MAX_ATTACHMENT_COUNT = 8;
    private static final long C_MAX_ATTACHMENT_SIZE_BYTES = 209715200;

    public MessageSenderVK(
            final String token,
            final long peerId,
            final String text,
            final List<AttachmentData> uploadingAttachmentList,
            final MessageSendingCallback callback,
            final ContentResolver contentResolver)
    {
        super(token, peerId, text, uploadingAttachmentList, callback, contentResolver);
    }

    private Error getUploadingUrlForPhoto(
            final VKAPIAttachment vkAPIAttachment,
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        Response<ResponseAttachmentPhotoUploadServerWrapper> response =
                vkAPIAttachment.getPhotoUploadServer(m_token, m_peerId).execute();

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
            final VKAPIAttachment vkAPIAttachment,
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        Response<ResponseAttachmentDocUploadServerWrapper> response =
                vkAPIAttachment.getDocUploadServer(m_token, m_peerId).execute();

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
            final VKAPIAttachment vkAPIAttachment,
            final AttachmentData attachmentData,
            ObjectWrapper<ResponseAttachmentBaseUploadServerBody> resultUploadingServerDataWrapper)
            throws IOException
    {
        switch (attachmentData.getType()) {
            case IMAGE: return getUploadingUrlForPhoto(vkAPIAttachment, resultUploadingServerDataWrapper);
            case DOC:   return getUploadingUrlForDoc(vkAPIAttachment, resultUploadingServerDataWrapper);
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
            return new Error("Obtained uploading result wasn't successful!", true);

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
            final VKAPIAttachment vkAPIAttachment,
            final ResponseAttachmentPhotoUploadServerBody responseAttachmentPhotoUploadServerBody,
            final ResponseAttachmentPhotoUploaded responseAttachmentPhotoUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        Response<ResponseAttachmentPhotoSaveWrapper> response =
                vkAPIAttachment.saveUploadedPhoto(m_token,
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
            final VKAPIAttachment vkAPIAttachment,
            final ResponseAttachmentDocUploadServerBody responseAttachmentDocUploadServerBody,
            final ResponseAttachmentDocUploaded responseAttachmentDocUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        Response<ResponseAttachmentDocSaveWrapper> response =
                vkAPIAttachment.saveUploadedDoc(
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
            final VKAPIAttachment vkAPIAttachment,
            final AttachmentData attachmentData,
            final ResponseAttachmentBaseUploadServerBody responseAttachmentUploadServerBody,
            final ResponseAttachmentUploaded responseAttachmentUploaded,
            ObjectWrapper<String> attachmentIdWrapper)
            throws IOException
    {
        switch (attachmentData.getType()) {
            case IMAGE: return saveSentPhotoAttachment(
                    vkAPIAttachment,
                    (ResponseAttachmentPhotoUploadServerBody) responseAttachmentUploadServerBody,
                    (ResponseAttachmentPhotoUploaded) responseAttachmentUploaded,
                    attachmentIdWrapper);
            case DOC: return saveSentDocAttachment(
                    vkAPIAttachment,
                    (ResponseAttachmentDocUploadServerBody) responseAttachmentUploadServerBody,
                    (ResponseAttachmentDocUploaded) responseAttachmentUploaded,
                    attachmentIdWrapper);
        }

        return new Error(
                "Cannot save content of a provided attachment!",
                true);
    }

    private VKAPIAttachment generateVKAPIAttachment() {
        Gson responseAttachmentDocSaveBodyGson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentDocSaveBody.class,
                new ResponseAttachmentDocSaveBodyDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create(responseAttachmentDocSaveBodyGson))
                .build();

        VKAPIAttachment vkapiAttachment = retrofit.create(VKAPIAttachment.class);

        return vkapiAttachment;
    }

    private Error uploadAttachments(
            final VKAPIInterface vkAPI,
            ObjectWrapper<String> resultAttachmentListStringWrapper)
    {
        if (m_uploadingAttachmentList == null)
            return null;
        if (m_uploadingAttachmentList.isEmpty())
            return null;

        VKAPIAttachment vkAPIAttachment = generateVKAPIAttachment();

        StringBuilder resultAttachmentListString = new StringBuilder();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .writeTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .readTimeout(C_TIMEOUT_SEC_VALUE, TimeUnit.SECONDS)
                .build();

        int attachmentCount = m_uploadingAttachmentList.size();

        try {
            for (int i = 0; i < attachmentCount; ++i) {
                final AttachmentData uploadingAttachmentData = m_uploadingAttachmentList.get(i);
                Error error = null;

                ObjectWrapper<ResponseAttachmentBaseUploadServerBody> responseUploadServerWrapper =
                        new ObjectWrapper<>();

                error = getAttachmentUploadingUrl(
                            vkAPIAttachment,
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
                            vkAPIAttachment,
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

        resultAttachmentListStringWrapper.setValue(resultAttachmentListString.toString());

        return null;
    }

    @Override
    protected Error doInBackground(Void... voids) {
        VKAPIInterface vkAPI = (VKAPIInterface) APIStore.getAPIInstance();

        if (vkAPI == null)
            return new Error("API hasn't been initialized!", true);

        try {
            ObjectWrapper<String> resultAttachmentListStringWrapper =
                    new ObjectWrapper<>();

            Error uploadAttachmentsError =
                    uploadAttachments(vkAPI, resultAttachmentListStringWrapper);

            if (uploadAttachmentsError != null)
                return uploadAttachmentsError;

            Response<ResponseSendMessageWrapper> response
                    = vkAPI.sendMessage(
                            m_token,
                            m_peerId,
                            m_text,
                            resultAttachmentListStringWrapper.getValue()).execute();

            if (!response.isSuccessful())
                return new Error("Message Sending request hasn't been accomplished!", true);
            if (response.body().error != null)
                return new Error(response.body().error.message, true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error(e.getMessage(), true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Error error) {
        if (error == null)
            m_callback.onMessageSent();
        else
            m_callback.onMessageSendingError(error);
    }
}
