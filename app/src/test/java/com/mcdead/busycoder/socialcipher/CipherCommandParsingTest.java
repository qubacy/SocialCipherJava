package com.mcdead.busycoder.socialcipher;

import static org.junit.Assert.assertEquals;

import androidx.core.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.CipherContext;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeySize;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataSessionSet;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser.CipherCommandDataParser;
import com.mcdead.busycoder.socialcipher.cipher.utility.CipherKeyUtility;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CipherCommandParsingTest {
    private String generateInitRequestCipherCommandString(
            final CipherConfiguration correctCipherConfig,
            final long creationTime)
    {
        StringBuilder result =
                new StringBuilder().
                        append(String.valueOf(CipherCommandType.CIPHER_SESSION_INIT_REQUEST.getId())).
                        append(CommandContext.C_SECTION_DIVIDER_CHAR);

        if (correctCipherConfig == null) {
            result.
                append(String.valueOf(-1)).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(-1)).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(-1)).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(-1));

        } else {
            result.
                append(String.valueOf(correctCipherConfig.getAlgorithm().getId())).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(correctCipherConfig.getMode().getId())).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(correctCipherConfig.getPadding().getId())).
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(correctCipherConfig.getKeySize().getId()));
        }

        return result.
                append(CommandContext.C_SECTION_DIVIDER_CHAR).
                append(String.valueOf(creationTime)).toString();
    }

    private String generateInitCompletedCipherCommandString(
            final HashMap<Long, Integer> peerIdSideIdHashMap,
            final String publicKeyData)
    {
        StringBuilder commandString =
                new StringBuilder().
                    append(String.valueOf(CipherCommandType.CIPHER_SESSION_INIT_COMPLETED.getId())).
                    append(CommandContext.C_SECTION_DIVIDER_CHAR);

        int peerIdSideIdPairListSize = peerIdSideIdHashMap.size();
        int index = 0;

        for (final Map.Entry<Long, Integer> peerIdSideIdPair : peerIdSideIdHashMap.entrySet()) {
            commandString.
                    append(String.valueOf(peerIdSideIdPair.getKey())).
                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                    append(String.valueOf(peerIdSideIdPair.getValue())).
                    append((index + 1 == peerIdSideIdPairListSize ?
                            "" :
                            CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            ++index;
        }

        commandString.append(CommandContext.C_SECTION_DIVIDER_CHAR);
        commandString.append(publicKeyData);

        return commandString.toString();
    }

    private String generateInitRouteCipherCommandString(
            final HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap)
    {
        StringBuilder commandString =
                new StringBuilder().
                        append(String.valueOf(CipherCommandType.CIPHER_SESSION_INIT_ROUTE.getId())).
                        append(CommandContext.C_SECTION_DIVIDER_CHAR);

        int sideIdRouteIdDataHashMapSize = sideIdRouteIdDataHashMap.size();
        int index = 0;

        for (final Map.Entry<Integer, Pair<Integer, byte[]>> sideIdRouteIdData :
                sideIdRouteIdDataHashMap.entrySet())
        {
            commandString.
                    append(sideIdRouteIdData.getKey()).
                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                    append(sideIdRouteIdData.getValue().first).
                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                    append(Base64.getEncoder().encodeToString(
                            sideIdRouteIdData.getValue().second)).
                    append((index + 1 == sideIdRouteIdDataHashMapSize ?
                            "" :
                            CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            ++index;
        }

        return commandString.toString();
    }

    @Test
    public void parsingCipherCommandDataMatrix() {
        List<CipherParserTestData> cipherParserTestDataList =
            new ArrayList<CipherParserTestData>()
            {
                {
                    CipherConfiguration correctCipherConfig =
                            CipherConfiguration.getInstance(
                                    CipherAlgorithm.AES,
                                    CipherMode.CTR,
                                    CipherPadding.NO_PADDING,
                                    CipherKeySize.KEY_256);
                    long creationTime = System.currentTimeMillis();

                    // CORRECT COMMANDS:

                    add(new CipherParserTestData(
                            generateInitRequestCipherCommandString(correctCipherConfig, creationTime),
                            null,
                            new ObjectWrapper<>(
                                    CipherCommandDataInitRequest.getInstance(
                                            correctCipherConfig,
                                            creationTime)
                            )));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(String.valueOf(CipherCommandType.CIPHER_SESSION_INIT_ACCEPT.getId())).
                                    toString(),
                            null,
                            new ObjectWrapper<>(
                                    CipherCommandDataInitAccept.getInstance()
                            )));

                    HashMap<Long, Integer> peerIdSideIdHashMap =
                            new HashMap<Long, Integer>() {
                                {
                                    put(1L, 0);
                                    put(2L, 1);
                                }
                            };
                    String correctPublicKeyData =
                            "MIIDJjCCAhgGCSqGSIb3DQEDATCCAgkCggEBAJVHXPXZPllsP80dkCrdAvQn9fPHIQMTu0X7TVuy5f4cvWeM1LvdhMmDa+HzHAd3clrrbC/Di4X0gHb6drzYFGzImm+y9wbdcZiYwgg9yNiW+EBi4snJTRN7BUqNgJatuNUZUjmO7KhSoK8S34Pkdapl1OwMOKlWDVZhGG/5i5/J62Du6LAwN2sja8c746zb10/WHB0kdfowd7jwgEZ4gf9+HKVv7gZteVBq3lHtu1RDpWOSfbxLpSAIZ0YXXIiFkl68ZMYUeQZ3NJaZDLcU7GZzBOJh+u4zs8vfAI4MP6kGUNl9OQnJJ1v0rIb/yz0D5t/IraWTQkLdbTvMoqQGywsCggEAQt67naWz2IzJVuCHh+w/Ogm7pfSLiJp0qvUxdKoPvn48W4/NelO+9WOw6YVgMolgqVF/QBTTMl/Hlivx4Ek3DXbRMUp2E355Lz8NuFnQleSluTICTweezy7wnHl0UrB3DhNQeC7Vfd95SXnc7yPLlvGDBhllxOvJPJxxxWuSWVWnX5TMzxRJrEPVhtC+7kMlGwsihzSdaN4NFEQD8T6AL0FG2ILgV68ZtvYnXGZ2yPoOPKJxOjJX/Rsn0GOfaV40fY0c+ayBmibKmwTLDrm3sDWYjRW7rGUhKlUjnPx+WPrjjXJQq5mR/7yXE0Al/ozgTEOZrZZWm+kaVG9JeGk8egOCAQYAAoIBAQCGSu/Eyxg5oAG5BwHVEQzLMc6GVGwtPCZPFsD5UzyYLYnHPolyB2CzFWHNQBHN7Jj5jPZtUVvkY+KNKogscJdOjrbrioXxylqTJ/w/3/U9Q1/HuyK2PGlFDpK/dryeTr62ZIJDYa62NVchVBWSikPEFWk2ZO+CNX+RKgJs4SSfvMl52jn1U0QSAbIlxEuJX0D0s3KrbC+8Mx7G1LS05c8to2toQ0ItqsWgR/HpAt9rThLEVh5Z2frVXN5ndeUGeXvCpIPjnGKDLB7ImytKaEzJT3SA5K5qpQRIKwwrw3nf+IQzBxdMDmuHQd7fgvT/WMsuLLvEM1ecuk3YvGxvBYQ5";
                    byte[] publicKeyBytes =
                            Base64.getDecoder().decode(correctPublicKeyData.getBytes(StandardCharsets.UTF_8));
                    PublicKey publicKey =
                            CipherKeyUtility.generatePublicKeyWithBytes(
                                    CipherContext.C_ALGORITHM,
                                    publicKeyBytes);

                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(
                                    peerIdSideIdHashMap,
                                    correctPublicKeyData),
                            null,
                            new ObjectWrapper<>(
                                    CipherCommandDataInitRequestCompleted.getInstance(peerIdSideIdHashMap, publicKey)
                            )));

                    HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap =
                            new HashMap<Integer, Pair<Integer, byte[]>>() {
                                {
                                    put(0, new Pair<>(1, publicKeyBytes));
                                }
                            };

                    add(new CipherParserTestData(
                            generateInitRouteCipherCommandString(sideIdRouteIdDataHashMap),
                            null,
                            new ObjectWrapper<>(
                                    CipherCommandDataInitRoute.getInstance(sideIdRouteIdDataHashMap)
                            )
                    ));

                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(String.valueOf(CipherCommandType.CIPHER_SESSION_SET.getId())).
                                    toString(),
                            null,
                            new ObjectWrapper<>(
                                    CipherCommandDataSessionSet.getInstance()
                            )));

                    // INCORRECT COMMANDS
                    // OVERALL:

                    add(new CipherParserTestData(
                            "",
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.EMPTY_DATA_STRING),
                            new ObjectWrapper<>()));
                    add(new CipherParserTestData(
                            null,
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_ARGS),
                            new ObjectWrapper<>()));
                    add(new CipherParserTestData(
                            "-1",
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_TYPE),
                            new ObjectWrapper<>()));

                    // INIT_REQUEST:

                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_REQUEST.getId()).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_REQUEST),
                            new ObjectWrapper<>()));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_REQUEST.getId())
                                    .append(CommandContext.C_SECTION_DIVIDER_CHAR)
                                    .append("-1").
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_REQUEST),
                            new ObjectWrapper<>()));
                    add(new CipherParserTestData(
                            generateInitRequestCipherCommandString(null, creationTime),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_REQUEST),
                            new ObjectWrapper<>()));
                    add(new CipherParserTestData(
                            generateInitRequestCipherCommandString(correctCipherConfig, -1),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.FAILED_INIT_REQUEST_GENERATION),
                            new ObjectWrapper<>()));

                    // INIT_COMPLETED:

                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_COMPLETED.getId()).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_COMPLETED),
                            new ObjectWrapper<>()
                    ));

                    HashMap<Long, Integer> incorrectPeerIdSideIdHashMap =
                        new HashMap<Long, Integer>() {
                            {
                                put(1L, 0);
                            }
                        };

                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(incorrectPeerIdSideIdHashMap, correctPublicKeyData),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIRS_COUNT),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(peerIdSideIdHashMap, ""),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_COMPLETED),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_COMPLETED.getId()).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(String.valueOf(1)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(correctPublicKeyData).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIRS_COUNT),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_COMPLETED.getId()).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(String.valueOf(1)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    append(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR).
                                    append(String.valueOf(2)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(correctPublicKeyData).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_PARTS_COUNT),
                            new ObjectWrapper<>()
                    ));

                    incorrectPeerIdSideIdHashMap.put(2L, -1);

                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(incorrectPeerIdSideIdHashMap, correctPublicKeyData),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_DATA),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(peerIdSideIdHashMap, Base64.getEncoder().encodeToString(new byte[]{1, 42})),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.FAILED_PUBLIC_KEY_CREATION),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            generateInitCompletedCipherCommandString(peerIdSideIdHashMap, "something.."),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.NULL_PUBLIC_KEY_BYTES),
                            new ObjectWrapper<>()
                    ));

                    // INIT_ROUTE:

                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_ROUTE.getId()).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_INIT_ROUTE),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_ROUTE.getId()).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(" ").
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_PARTS_COUNT),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_ROUTE.getId()).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_PARTS_COUNT),
                            new ObjectWrapper<>()
                    ));
                    add(new CipherParserTestData(
                            new StringBuilder().
                                    append(CipherCommandType.CIPHER_SESSION_INIT_ROUTE.getId()).
                                    append(CommandContext.C_SECTION_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append(String.valueOf(0)).
                                    append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR).
                                    append("something").
                                    toString(),
                            CipherCommandDataParser.C_ERROR_HASH_MAP.get(CipherCommandDataParser.ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_DATA),
                            new ObjectWrapper<>()
                    ));
                }
            };

        for (final CipherParserTestData cipherParserTestData : cipherParserTestDataList) {
            ObjectWrapper<CipherCommandData> curCipherCommandData = new ObjectWrapper<>();
            Error curError =
                    CipherCommandDataParser.parseCipherCommandData(
                            cipherParserTestData.cipherCommandString,
                            curCipherCommandData);

            assertEquals(cipherParserTestData.error, curError);
            assertEquals(cipherParserTestData.cipherCommandDataWrapper, curCipherCommandData);
        }
    }

    private static class CipherParserTestData {
        final public String cipherCommandString;
        final public Error error;
        final public ObjectWrapper<CipherCommandData> cipherCommandDataWrapper;

        public CipherParserTestData(
                final String cipherCommandString)
        {
            this.cipherCommandString = cipherCommandString;
            this.error = null;
            this.cipherCommandDataWrapper = new ObjectWrapper<>();
        }

        public CipherParserTestData(
                final String cipherCommandString,
                final Error error,
                final ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
        {
            this.cipherCommandString = cipherCommandString;
            this.error = error;
            this.cipherCommandDataWrapper = cipherCommandDataWrapper;
        }
    }
}
