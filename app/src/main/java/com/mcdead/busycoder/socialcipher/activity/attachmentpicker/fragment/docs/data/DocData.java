package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.fragment.docs.data;

import android.net.Uri;

public class DocData {
    private Uri m_contentUri = null;
    private String m_displayName = null;

    public DocData(
            final Uri contentUri,
            final String displayName)
    {
        m_contentUri = contentUri;
        m_displayName = displayName;
    }

    public Uri getContentUri() {
        return m_contentUri;
    }

    public String getDisplayName() {
        return m_displayName;
    }
}
