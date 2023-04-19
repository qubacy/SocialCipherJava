package com.mcdead.busycoder.socialcipher.setting.network;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.mcdead.busycoder.socialcipher.api.APIType;
import com.mcdead.busycoder.socialcipher.setting.SettingsBase;
import com.mcdead.busycoder.socialcipher.setting.SettingsContext;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SettingsNetwork extends SettingsBase
{
    private static final String C_TOKEN_PROP_NAME = "token";
    private static final String C_API_TYPE_PROP_NAME = "apiType";

    private static SettingsNetwork s_instance = null;

    private String m_token = null;
    private APIType m_apiType = null;
    private long m_localPeerId = 0;

    private SettingsNetwork() {

    }

    public static synchronized SettingsNetwork getInstance() {
        if (s_instance == null)
            s_instance = new SettingsNetwork();

        return s_instance;
    }

    public String getToken() {
        return m_token;
    }

    public boolean setToken(final String newToken) {
        if (newToken == null) return false;
        if (newToken.isEmpty()) return false;

        m_token = newToken;

        return true;
    }

    public APIType getAPIType() {
        return m_apiType;
    }

    public boolean setAPIType(final APIType type) {
        if (type == null) return false;

        m_apiType = type;

        return true;
    }

    public long getLocalPeerId() {
        return m_localPeerId;
    }

    public boolean setLocalPeerId(final long localPeerId) {
        if (localPeerId == 0) return false;

        m_localPeerId = localPeerId;

        return true;
    }

    @Override
    public String getFilePath() {
        String basePath = super.getFilePath();

        if (basePath == null) return null;

        return basePath + "network." + SettingsContext.C_FILE_DEFAULT_EXTENSION;
    }

    @Override
    public boolean load() {
        File fileSettings = new File(getFilePath());

        if (!fileSettings.exists()) return true;
        if (!fileSettings.canRead()) return false;

        try (JsonReader reader = new JsonReader(new FileReader(fileSettings))) {
            reader.beginObject();

            while (reader.hasNext()) {
                String propName = reader.nextName();

                switch (propName) {
                    case C_TOKEN_PROP_NAME: {
                        m_token = reader.nextString();

                        break;
                    }
                    case C_API_TYPE_PROP_NAME: {
                        APIType type = APIType.getAPITypeById(reader.nextInt());

                        if (type == null) return false;

                        m_apiType = type;

                        break;
                    }
                    default:
                        return false;
                }
            }

            reader.endObject();

        }
        catch (Throwable e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean store() {
        String s = getFilePath();
        File fileSettings = new File(getFilePath());

        try (JsonWriter writer = new JsonWriter(new FileWriter(fileSettings))) {
            writer.beginObject()
                    .name(C_TOKEN_PROP_NAME)
                    .value(m_token)
                    .name(C_API_TYPE_PROP_NAME)
                    .value(m_apiType.getId())
                    .endObject();

        }
        catch (Throwable e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean isFullyInitialized() {
        return (m_token != null && m_apiType != null);
    }

    @Override
    public void setDefaults() {
        m_apiType = APIType.VK;
        m_token = new String();
    }
}
