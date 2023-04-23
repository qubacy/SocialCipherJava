package com.mcdead.busycoder.socialcipher.data.utility.message;

import com.mcdead.busycoder.socialcipher.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.data.entity.attachment.AttachmentEntityBase;
import com.mcdead.busycoder.socialcipher.data.entity.message.MessageEntity;

import java.util.List;

public class MessageTextGenerator {
    private static final char C_LABEL_WRAPPER_CHAR_LEFT = '(';
    private static final char C_LABEL_WRAPPER_CHAR_RIGHT = ')';

    private static final String C_NO_DATA_TO_SHOW_LABEL_TEXT =
            C_LABEL_WRAPPER_CHAR_LEFT + "No data to show" + C_LABEL_WRAPPER_CHAR_RIGHT;
    private static final String C_ATTACHMENT_COUNT_LABEL_TEXT =
            C_LABEL_WRAPPER_CHAR_LEFT + "Some attachments: ";

    public static String generateChatPreviewMessageText(final MessageEntity lastMessage) {
        return generateMessageText(lastMessage, true);
    }

    public static String generateChatMessageText(final MessageEntity message) {
        return generateMessageText(message, false);
    }

    private static String generateMessageText(
            final MessageEntity message,
            final boolean isChatPreview)
    {
        if (!message.getMessage().isEmpty())
            return message.getMessage();

        StringBuilder messageText = new StringBuilder();

        List<ResponseAttachmentInterface> attachmentToLoadList =
                message.getAttachmentToLoad();
        List<AttachmentEntityBase> attachmentList =
                message.getAttachments();

        if (attachmentToLoadList != null || !attachmentList.isEmpty()) {
            int attachmentListSize =
                    (attachmentToLoadList == null ?
                    attachmentList.size() :
                            (attachmentToLoadList.isEmpty() ?
                            attachmentList.size() :
                            attachmentToLoadList.size()));

            if (isChatPreview) {
                messageText.append(C_ATTACHMENT_COUNT_LABEL_TEXT);
                messageText.append(String.valueOf(attachmentListSize));
                messageText.append(C_LABEL_WRAPPER_CHAR_RIGHT);
            } else
                messageText.append("");
        } else
            messageText.append(C_NO_DATA_TO_SHOW_LABEL_TEXT);

        return messageText.toString();
    }
}
