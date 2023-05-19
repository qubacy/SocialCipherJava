package com.mcdead.busycoder.socialcipher.client.data.entity.message;

import com.mcdead.busycoder.socialcipher.client.api.common.gson.chat.ResponseAttachmentInterface;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.id.MessageIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.message.id.MessageIdCheckerGenerator;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class MessageEntityGenerator {
    public static MessageEntity generateMessage(
            final long id,
            final UserEntity senderUser,
            final String message,
            final long timestamp,
            final boolean isCiphered,
            final List<ResponseAttachmentInterface> attachmentsToLoadList)
    {
        if (senderUser == null || timestamp == 0)
            return null;

        MessageIdChecker messageIdChecker = MessageIdCheckerGenerator.generateMessageIdChecker();

        if (messageIdChecker == null)
            return null;
        if (!messageIdChecker.isValid(id))
            return null;

        List<ResponseAttachmentInterface> attachmentToLoadListChecked =
                new ArrayList<>();

        if (attachmentsToLoadList != null)
            if (!attachmentsToLoadList.isEmpty()) {
                for (final ResponseAttachmentInterface attachmentToLoad : attachmentsToLoadList) {
                    if (attachmentToLoad == null) continue;

                    attachmentToLoadListChecked.add(attachmentToLoad);
                }
            }

        if (attachmentToLoadListChecked.isEmpty() && message == null)
            return null;

        return new MessageEntity(
                id,
                senderUser,
                message,
                timestamp,
                isCiphered,
                (attachmentToLoadListChecked.isEmpty() ?
                        null :
                        attachmentToLoadListChecked)
        );
    }
}
