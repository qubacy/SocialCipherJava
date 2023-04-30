package com.mcdead.busycoder.socialcipher.setting.cipher;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherLibrary;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.setting.SettingsBase;
import com.mcdead.busycoder.socialcipher.setting.SettingsContext;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SettingsCipher extends SettingsBase {
    public static final String C_LIBRARY_ID_PROP_NAME = "libraryId";

    public static final String C_ALGO_ID_PROP_NAME = "algorithmId";
    public static final String C_MODE_ID_PROP_NAME = "modeId";
    public static final String C_PADDING_ID_PROP_NAME = "paddingId";
    public static final String C_KEY_SIZE_ID_PROP_NAME = "keySizeId";

    private static SettingsCipher s_instance = null;

    private CipherLibrary m_library = null;

    private CipherConfiguration m_configuration = null;

//    private CipherAlgorithm m_algorithm = null;
//    private CipherMode m_mode = null;
//    private CipherPadding m_padding = null;
//    private CipherKeySize m_keySize = null;

    private SettingsCipher() {

    }

    public static synchronized SettingsCipher getInstance() {
        if (s_instance == null)
            s_instance = new SettingsCipher();

        return s_instance;
    }

    public CipherLibrary getLibrary() {
        return m_library;
    }

    public CipherAlgorithm getAlgorithm() {
        return m_configuration.getAlgorithm();
    }

    public CipherMode getMode() {
        return m_configuration.getMode();
    }

    public CipherPadding getPadding() {
        return m_configuration.getPadding();
    }

    public CipherKeySize getKeySize() {
        return m_configuration.getKeySize();
    }

    public CipherConfiguration getConfiguration() {
        return m_configuration;
    }

    public boolean setLibrary(final CipherLibrary library) {
        if (library == null) return false;

        m_library = library;

        return true;
    }

    public boolean setAlgorithm(final CipherAlgorithm algorithm) {
        if (algorithm == null) return false;

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        algorithm,
                        m_configuration.getMode(),
                        m_configuration.getPadding(),
                        m_configuration.getKeySize());

        return setConfiguration(cipherConfiguration);
    }

    public boolean setMode(final CipherMode mode) {
        if (mode == null) return false;

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        m_configuration.getAlgorithm(),
                        mode,
                        m_configuration.getPadding(),
                        m_configuration.getKeySize());

        return setConfiguration(cipherConfiguration);
    }

    public boolean setPadding(final CipherPadding padding) {
        if (padding == null) return false;

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        m_configuration.getAlgorithm(),
                        m_configuration.getMode(),
                        padding,
                        m_configuration.getKeySize());

        return setConfiguration(cipherConfiguration);
    }

    public boolean setKeySize(final CipherKeySize keySize) {
        if (keySize == null) return false;

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        m_configuration.getAlgorithm(),
                        m_configuration.getMode(),
                        m_configuration.getPadding(),
                        keySize);

        return setConfiguration(cipherConfiguration);
    }

    public boolean setConfiguration(final CipherConfiguration configuration) {
        if (configuration == null) return false;

        m_configuration = configuration;

        return true;
    }

    @Override
    public String getFilePath() {
        String basePath = super.getFilePath();

        if (basePath == null) return null;

        return basePath + "cipher." + SettingsContext.C_FILE_DEFAULT_EXTENSION;
    }

    @Override
    public boolean load() {
        File fileSettings = new File(getFilePath());

        if (!fileSettings.exists()) return true;
        if (!fileSettings.canRead()) return false;

        CipherAlgorithm algo = null;
        CipherMode mode = null;
        CipherPadding padding = null;
        CipherKeySize keySize = null;

        try (JsonReader reader = new JsonReader(new FileReader(fileSettings))) {
            reader.beginObject();

            while (reader.hasNext()) {
                String propName = reader.nextName();

                switch (propName) {
                    case C_LIBRARY_ID_PROP_NAME: {
                        CipherLibrary lib =
                                CipherLibrary.getLibraryById(reader.nextInt());

                        if (lib == null) return false;

                        m_library = lib;

                        break;
                    }
                    case C_ALGO_ID_PROP_NAME: {
                        algo = CipherAlgorithm.getAlgorithmById(reader.nextInt());

                        if (algo == null) return false;

                        //m_algorithm = algo;

                        break;
                    }
                    case C_MODE_ID_PROP_NAME: {
                        mode = CipherMode.getModeById(reader.nextInt());

                        if (mode == null) return false;

                        //m_mode = mode;

                        break;
                    }
                    case C_PADDING_ID_PROP_NAME: {
                        padding = CipherPadding.getPaddingById(reader.nextInt());

                        if (padding == null) return false;

                        //m_padding = padding;

                        break;
                    }
                    case C_KEY_SIZE_ID_PROP_NAME: {
                        keySize = CipherKeySize.getCipherKeySizeById(reader.nextInt());

                        if (keySize == null) return false;

                        //m_keySize = keySize;

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

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(algo, mode, padding, keySize);

        if (cipherConfiguration == null)
            return false;

        m_configuration = cipherConfiguration;

        return true;
    }

    @Override
    public boolean store() {
        String s = getFilePath();
        File fileSettings = new File(getFilePath());

        try (JsonWriter writer = new JsonWriter(new FileWriter(fileSettings))) {
            writer.beginObject()
                    .name(C_LIBRARY_ID_PROP_NAME)
                    .value(m_library.getId())
                    .name(C_ALGO_ID_PROP_NAME)
                    .value(m_configuration.getAlgorithm().getId())
                    .name(C_MODE_ID_PROP_NAME)
                    .value(m_configuration.getMode().getId())
                    .name(C_PADDING_ID_PROP_NAME)
                    .value(m_configuration.getPadding().getId())
                    .name(C_KEY_SIZE_ID_PROP_NAME)
                    .value(m_configuration.getKeySize().getId())
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
        return (m_library != null &&
                m_configuration != null);
    }

    @Override
    public void setDefaults() {
        m_library = CipherLibrary.JAVAX;

        m_configuration =
                CipherConfiguration.getInstance(
                    CipherAlgorithm.AES,
                    CipherMode.CTR,
                    CipherPadding.NO_PADDING,
                    CipherKeySize.KEY_256);
    }
}
