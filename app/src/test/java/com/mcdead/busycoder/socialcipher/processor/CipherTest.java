package com.mcdead.busycoder.socialcipher.processor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeyGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererGenerator;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CipherTest {
    private static final String C_BIG_FILE_PATH =
            "C:/Users/MCDead/AndroidStudioProjects/SocialCipher/app/src/test/res/picture.jpg";

    private CipherConfiguration m_cipherConfiguration = null;
    private byte[] m_keyBytes = null;

    @Before
    public void prepareCommonData() {
        m_cipherConfiguration =
                CipherConfiguration.getInstance(
                        CipherAlgorithm.AES,
                        CipherMode.CTR,
                        CipherPadding.NO_PADDING,
                        CipherKeySize.KEY_256);
        m_keyBytes = new byte[]{0, 124, 52, 12};
    }

    private byte[] loadBigData() {
        File file = new File(C_BIG_FILE_PATH);

        if (!file.exists()) return null;

        try (FileInputStream in = new FileInputStream(file)) {
            int availableByteCount = in.available();
            byte[] fileContent = new byte[availableByteCount];

            if (in.read(fileContent) != availableByteCount)
                return null;

            return fileContent;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void cipheringDecipheringDataJavax() {
        CipherKey cipherKey =
                CipherKeyGenerator.
                        generateCipherKeyWithConfiguration(
                                m_cipherConfiguration, m_keyBytes);

        assertNotNull(cipherKey);

        CiphererBase ciphererBase =
                CiphererGenerator.
                        generateCiphererWithConfigurationJavax(m_cipherConfiguration, cipherKey);

        assertNotNull(ciphererBase);

        List<byte[]> dataList = new ArrayList<byte[]>() {
            {
                add("some data..".getBytes(StandardCharsets.UTF_8));

                byte[] bigData = loadBigData();

                assertNotNull(bigData);

                add(bigData);
            }
        };

        for (final byte[] data : dataList) {
            byte[] encryptedData = ciphererBase.encryptBytes(data);

            assertNotNull(encryptedData);

            byte[] decryptedData = ciphererBase.decryptBytes(encryptedData);

            assertNotNull(decryptedData);
            assertArrayEquals(data, decryptedData);
        }
    }
}
