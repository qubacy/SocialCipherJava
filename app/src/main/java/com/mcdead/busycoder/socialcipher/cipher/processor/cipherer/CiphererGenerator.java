package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherLibrary;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.javax.CiphererAESJavax;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.javax.CiphererBaseJavax;
import com.mcdead.busycoder.socialcipher.setting.cipher.SettingsCipher;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class CiphererGenerator {
    public static CiphererBase generateCiphererWithConfiguration(
            final CipherConfiguration cipherConfiguration,
            final CipherKey cipherKey)
    {
        SettingsCipher settingsCipher = SettingsCipher.getInstance();

        if (settingsCipher == null)
            return null;

        CipherLibrary library = settingsCipher.getLibrary();

        switch (library) {
            case JAVAX: return generateCiphererWithConfigurationJavax(
                    cipherConfiguration, cipherKey);
        }

        return null;
    }

    public static CiphererBase generateDefaultCipherer(
            final CipherKey cipherKey)
    {
        // todo: getting ciphering configuration from settings..

        SettingsCipher settingsCipher = SettingsCipher.getInstance();

        if (settingsCipher == null)
            return null;

        CipherLibrary library = settingsCipher.getLibrary();

        CipherAlgorithm algorithm = settingsCipher.getAlgorithm();
        CipherMode mode = settingsCipher.getMode();
        CipherPadding padding = settingsCipher.getPadding();
        CipherKeySize keySize = settingsCipher.getKeySize();

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(algorithm, mode, padding, keySize);

        switch (library) {
            case JAVAX: return generateCiphererWithConfigurationJavax(
                    cipherConfiguration, cipherKey);
        }

        return null;
    }

    public static CiphererBaseJavax generateCiphererWithConfigurationJavax(
            final CipherConfiguration cipherConfiguration,
            final CipherKey cipherKey)
    {
        if (cipherConfiguration == null)
            return null;

        StringBuilder cipherConfigString = new StringBuilder();

        cipherConfigString
                .append(cipherConfiguration.getAlgorithm().getName())
                .append('/')
                .append(cipherConfiguration.getMode().getName())
                .append('/')
                .append(cipherConfiguration.getPadding().getName());

        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(cipherConfigString.toString());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();

            return null;
        }

        if (cipher == null) return null;

        switch (cipherConfiguration.getAlgorithm()) {
            case AES: return new CiphererAESJavax(cipherKey, cipher);
        }

        return null;
    }
}
