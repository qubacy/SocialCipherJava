package com.mcdead.busycoder.socialcipher.processors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mcdead.busycoder.socialcipher.client.api.APIType;
import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIProvider;
import com.mcdead.busycoder.socialcipher.client.processor.network.tokenchecker.result.TokenCheckResult;
import com.mcdead.busycoder.socialcipher.client.processor.tokenchecker.TokenCheckerVK;
import com.mcdead.busycoder.socialcipher.client.processor.tokenchecker.TokenCheckerFactory;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class TokenCheckerVKTest {
    private static final String C_VALID_TOKEN =
            "YOUR_VALID_TOKEN";

    private VKAPIProvider m_vkAPIProvider = null;

    @Before
    public void setUp() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        settingsNetwork.setAPIType(APIType.VK);

        m_vkAPIProvider = new VKAPIProvider();
    }

    @Test
    public void checkingInvalidToken()
            throws ExecutionException, InterruptedException
    {
        List<String> invalidTokenList = new ArrayList<String>() {
            {
                add(null);
                add("");
                add("something...");
            }
        };

        for (final String invalidToken : invalidTokenList) {
            TokenCheckerVK tokenCheckerVK =
                    TokenCheckerFactory.generateTokenCheckerVK(
                            invalidToken, null, m_vkAPIProvider);

            tokenCheckerVK.execute();

            TokenCheckResult checkingResult = tokenCheckerVK.get();

            assertNotNull(checkingResult);

            assertNull(checkingResult.getLocalUser());
            assertNotNull(checkingResult.getError());
            assertFalse(checkingResult.isSucceeded());
        }
    }

    @Test
    public void checkingValidToken()
            throws ExecutionException, InterruptedException
    {
        TokenCheckerVK tokenCheckerVK =
                TokenCheckerFactory.generateTokenCheckerVK(
                        C_VALID_TOKEN, null, m_vkAPIProvider);

        tokenCheckerVK.execute();

        TokenCheckResult checkingResult = tokenCheckerVK.get();

        assertNotNull(checkingResult);

        assertNull(checkingResult.getError());
        assertTrue(checkingResult.isSucceeded());
        assertNotNull(checkingResult.getLocalUser());
    }
}
