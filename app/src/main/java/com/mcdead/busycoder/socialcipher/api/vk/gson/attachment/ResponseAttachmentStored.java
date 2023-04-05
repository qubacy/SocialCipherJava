package com.mcdead.busycoder.socialcipher.api.vk.gson.attachment;

/*
*
* attachmentID - an unique identificator used in getting attachment requests.
*
*/
public class ResponseAttachmentStored extends ResponseAttachmentBase {
    public String attachmentID;

    public ResponseAttachmentStored() {
        super();
    }

    public ResponseAttachmentStored(final String attachmentType,
                                    final String attachmentId)
    {
        super(attachmentType);

        this.attachmentID = attachmentId;
    }

    public ResponseAttachmentStored(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId)
    {
        super(attachmentType);

        this.attachmentID = String.valueOf(attachmentOwnerId)
                + '_' + String.valueOf(attachmentId);
    }

    public ResponseAttachmentStored(final String attachmentType,
                                    final long attachmentId,
                                    final long attachmentOwnerId,
                                    final String attachmentAccessToken)
    {
        super(attachmentType);

        this.attachmentID = String.valueOf(attachmentOwnerId)
                + '_' + String.valueOf(attachmentId)
                + '_' + attachmentAccessToken;
    }

    @Override
    public ResponseAttachmentType getAttachmentType() {
        return ResponseAttachmentType.STORED;
    }
}
