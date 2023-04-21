package com.mcdead.busycoder.socialcipher.data.entity.message;

import com.mcdead.busycoder.socialcipher.api.common.gson.chat.ResponseAttachmentInterface;

import java.util.ArrayList;
import java.util.List;

public class MessageEntityGenerator {
    public static MessageEntity generateMessage(
            final long id,
            final long fromPeerId,
            final String message,
            final long timestamp,
            final List<ResponseAttachmentInterface> attachmentsToLoadList)
    {
        if (id == 0 || fromPeerId == 0 || timestamp == 0)
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
                fromPeerId,
                message,
                timestamp,
                (attachmentToLoadListChecked.isEmpty() ?
                        null :
                        attachmentToLoadListChecked)
        );
    }
}
