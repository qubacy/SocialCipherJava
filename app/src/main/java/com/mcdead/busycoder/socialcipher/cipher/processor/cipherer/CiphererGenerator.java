package com.mcdead.busycoder.socialcipher.cipher.processor.cipherer;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
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
    public static CiphererBase generateCipherer(
            final CipherKey cipherKey)
    {
        // todo: getting ciphering configuration from settings..

        SettingsCipher settingsCipher = SettingsCipher.getInstance();

        if (settingsCipher == null)
            return null;

        CipherLibrary library = settingsCipher.getLibrary();

        switch (library) {
            case JAVAX: return generateCiphererJavax(settingsCipher, cipherKey);
        }

        return null;
    }

    private static CiphererBaseJavax generateCiphererJavax(
            final SettingsCipher settingsCipher,
            final CipherKey cipherKey)
    {
        CipherAlgorithm algorithm = settingsCipher.getAlgorithm();
        CipherMode mode = settingsCipher.getMode();
        CipherPadding padding = settingsCipher.getPadding();

        if (algorithm == null || mode == null || padding == null)
            return null;

        StringBuilder cipherConfigString = new StringBuilder();

        cipherConfigString
                .append(algorithm.getName())
                .append('/')
                .append(mode.getName())
                .append('/')
                .append(padding.getName());

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

        switch (algorithm) {
            case AES: return new CiphererAESJavax(cipherKey, cipher);
        }

        return null;
    }
}
