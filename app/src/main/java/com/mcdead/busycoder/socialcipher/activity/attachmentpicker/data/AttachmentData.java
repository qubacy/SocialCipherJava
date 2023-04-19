package com.mcdead.busycoder.socialcipher.activity.attachmentpicker.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.mcdead.busycoder.socialcipher.data.entity.attachment.attachmenttype.AttachmentType;

public class AttachmentData implements Parcelable {
    private AttachmentType m_type = null;
    private String m_mimeType = null;
    private String m_fileName = null;
    private Uri m_uri = null;

    public AttachmentData(
            final AttachmentType type,
            final String mimeType,
            final String fileName,
            final Uri uri)
    {
        m_type = type;
        m_mimeType = mimeType;
        m_fileName = fileName;
        m_uri = uri;
    }

    protected AttachmentData(Parcel in) {
        m_type = (AttachmentType) in.readSerializable();
        m_mimeType = in.readString();
        m_fileName = in.readString();
        m_uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<AttachmentData> CREATOR = new Creator<AttachmentData>() {
        @Override
        public AttachmentData createFromParcel(Parcel in) {
            return new AttachmentData(in);
        }

        @Override
        public AttachmentData[] newArray(int size) {
            return new AttachmentData[size];
        }
    };

    public Uri getUri() {
        return m_uri;
    }

    public AttachmentType getType() {
        return m_type;
    }

    public String getMimeType() {
        return m_mimeType;
    }

    public String getFileName() {
        return m_fileName;
    }

    public boolean setType(
            final AttachmentType type)
    {
        if (type == null || m_type != null)
            return false;

        m_type = type;

        return true;
    }

    public boolean setMimeType(
            final String mimeType)
    {
        if (mimeType == null || m_mimeType != null)
            return false;

        m_mimeType = mimeType;

        return true;
    }

    public boolean setFileName(
            final String fileName)
    {
        if (fileName == null || m_fileName != null)
            return false;
        if (fileName.isEmpty()) return false;

        m_fileName = fileName;

        return true;
    }

    public boolean setUri(
            final Uri uri)
    {
        if (uri == null || m_uri != null)
            return false;

        m_uri = uri;

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeSerializable(m_type);
        parcel.writeString(m_mimeType);
        parcel.writeString(m_fileName);
        parcel.writeParcelable(m_uri, i);
    }
}
