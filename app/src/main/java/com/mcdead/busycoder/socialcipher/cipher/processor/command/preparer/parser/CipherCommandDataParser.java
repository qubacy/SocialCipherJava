package com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser;

import android.util.Pair;

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
import com.mcdead.busycoder.socialcipher.cipher.utility.CipherKeyUtility;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;

public class CipherCommandDataParser {
    private static final int C_SHARED_SECTION_COUNT = 1;
    private static final int C_MIN_SECTION_COUNT = 1;

    private static final int C_INIT_REQUEST_SECTION_COUNT = 5;
    private static final int C_INIT_ACCEPT_SECTION_COUNT = 0;
    private static final int C_INIT_COMPLETED_SECTION_COUNT = 2;
    private static final int C_INIT_ROUTE_SECTION_COUNT = 1;
    private static final int C_SESSION_SET_SECTION_COUNT = 0;

    private static final int C_MIN_PEER_ID_SIDE_ID_PAIR_COUNT = 2;

    public static final HashMap<ErrorType, Error> C_ERROR_HASH_MAP =
            new HashMap<ErrorType, Error>()
            {
                {
                    put(ErrorType.INCORRECT_ARGS,
                            new Error("Cipher Command Data parsing args were incorrect!", true));
                    put(ErrorType.EMPTY_DATA_STRING,
                            new Error("Cipher Command Data string was empty!", true));
                    put(ErrorType.SMALL_SECTION_COUNT,
                            new Error("Serialized Cipher Command hadn't enough sections!", true));
                    put(ErrorType.INCORRECT_TYPE,
                            new Error("Serialized Cipher Command had an incorrect type!", true));

                    put(ErrorType.INCORRECT_INIT_REQUEST,
                            new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true));
                    put(ErrorType.INCORRECT_CIPHER_CONFIGURATION,
                            new Error("Incorrect Cipher Configuration has been provided!", true));
                    put(ErrorType.FAILED_INIT_REQUEST_GENERATION,
                            new Error("Cipher Command Data Init Request generating has been failed!", true));

                    put(ErrorType.INCORRECT_INIT_ACCEPT,
                            new Error("Incorrect Serialized Cipher Command Data Init Accept has been provided", true));
                    put(ErrorType.FAILED_INIT_ACCEPT_GENERATION,
                            new Error("Cipher Command Data Init Accept parsing has been failed!", true));

                    put(ErrorType.INCORRECT_INIT_COMPLETED,
                            new Error("Incorrect Serialized Cipher Command Data Init Completed has been provided", true));
                    put(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIRS_COUNT,
                            new Error("Peer Id Side Id pairs count was incorrect during parsing process!", true));
                    put(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_PARTS_COUNT,
                            new Error("Peer Id Side Id pair parts count was incorrect during parsing process!", true));
                    put(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_DATA,
                            new Error("Peer Id Side Id were incorrect during parsing process!", true));
                    put(ErrorType.NULL_PUBLIC_KEY_BYTES,
                            new Error("Public Key Bytes were null during parsing process!", true));
                    put(ErrorType.FAILED_PUBLIC_KEY_CREATION,
                            new Error("Public Key creation failed during parsing process!", true));
                    put(ErrorType.FAILED_INIT_COMPLETED_GENERATION,
                            new Error("Cipher Command Data Init Request Completed parsing has been failed!", true));

                    put(ErrorType.INCORRECT_INIT_ROUTE,
                            new Error("Incorrect Serialized Cipher Command Data Init Route has been provided", true));
                    put(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIRS_COUNT,
                            new Error("Route Id Data pairs count was incorrect during parsing process!", true));
                    put(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_PARTS_COUNT,
                            new Error("Route Id Data pair parts count was incorrect during parsing process!", true));
                    put(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_DATA,
                            new Error("Route Id Data were incorrect during parsing process!", true));
                    put(ErrorType.FAILED_INIT_ROUTE_GENERATION,
                            new Error("Cipher Command Data Init Route parsing has been failed!", true));

                    put(ErrorType.INCORRECT_SESSION_SET,
                            new Error("Incorrect Serialized Cipher Command Data Session Set has been provided", true));
                    put(ErrorType.FAILED_SESSION_SET_GENERATION,
                            new Error("Cipher Command Data Session Set parsing has been failed!", true));
                }
            };

    public static enum ErrorType {
        INCORRECT_ARGS,
        EMPTY_DATA_STRING,
        SMALL_SECTION_COUNT,
        INCORRECT_TYPE,

        INCORRECT_INIT_REQUEST,
        INCORRECT_CIPHER_CONFIGURATION,
        FAILED_INIT_REQUEST_GENERATION,

        INCORRECT_INIT_ACCEPT,
        FAILED_INIT_ACCEPT_GENERATION,

        INCORRECT_INIT_COMPLETED,
        INCORRECT_PEER_ID_SIDE_ID_PAIRS_COUNT,
        INCORRECT_PEER_ID_SIDE_ID_PAIR_PARTS_COUNT,
        INCORRECT_PEER_ID_SIDE_ID_PAIR_DATA,
        NULL_PUBLIC_KEY_BYTES,
        FAILED_PUBLIC_KEY_CREATION,
        FAILED_INIT_COMPLETED_GENERATION,

        INCORRECT_INIT_ROUTE,
        INCORRECT_ROUTE_ID_DATA_PAIRS_COUNT,
        INCORRECT_ROUTE_ID_DATA_PAIR_PARTS_COUNT,
        INCORRECT_ROUTE_ID_DATA_PAIR_DATA,
        FAILED_INIT_ROUTE_GENERATION,

        INCORRECT_SESSION_SET,
        FAILED_SESSION_SET_GENERATION
    };

    public static Error parseCipherCommandData(
            final String cipherCommandDataString,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (cipherCommandDataString == null || cipherCommandDataWrapper == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_ARGS);
        if (cipherCommandDataString.isEmpty())
            return C_ERROR_HASH_MAP.get(ErrorType.EMPTY_DATA_STRING);

        String[] cipherCommandSections =
                cipherCommandDataString.split(
                        String.valueOf(CommandContext.C_SECTION_DIVIDER_CHAR));

        if (cipherCommandSections.length < C_MIN_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.SMALL_SECTION_COUNT);

        int cipherCommandTypeId = Integer.parseInt(cipherCommandSections[0]);
        CipherCommandType cipherCommandType =
                CipherCommandType.getCommandTypeById(cipherCommandTypeId);

        if (cipherCommandType == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_TYPE);

        Error parsingError = null;

        switch (cipherCommandType) {
            case CIPHER_SESSION_INIT_REQUEST: {
                parsingError =
                        parseCipherCommandDataInitRequest(
                                cipherCommandSections,
                                cipherCommandDataWrapper);

                break;
            }
            case CIPHER_SESSION_INIT_ACCEPT: {
                parsingError =
                        parseCipherCommandDataInitAccept(
                                cipherCommandSections,
                                cipherCommandDataWrapper);

                break;
            }
            case CIPHER_SESSION_INIT_COMPLETED: {
                parsingError =
                        parseCipherCommandDataInitCompleted(
                                cipherCommandSections,
                                cipherCommandDataWrapper);

                break;
            }
            case CIPHER_SESSION_INIT_ROUTE: {
                parsingError =
                        parseCipherCommandDataInitRoute(
                                cipherCommandSections,
                                cipherCommandDataWrapper);

                break;
            }
            case CIPHER_SESSION_SET: {
                parsingError =
                        parseCipherCommandDataSessionSet(
                                cipherCommandSections,
                                cipherCommandDataWrapper
                        );

                break;
            }
        }

        if (parsingError != null)
            return parsingError;

        return null;
    }

    public static Error parseCipherCommandDataInitRequest(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_REQUEST_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_REQUEST);

        int cipherAlgorithmId = Integer.parseInt(serializedCipherCommandDataSections[1]);
        CipherAlgorithm cipherAlgorithm = CipherAlgorithm.getAlgorithmById(cipherAlgorithmId);

        if (cipherAlgorithm == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_REQUEST);

        int cipherModeId = Integer.parseInt(serializedCipherCommandDataSections[2]);
        CipherMode cipherMode = CipherMode.getModeById(cipherModeId);

        if (cipherMode == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_REQUEST);

        int cipherPaddingId = Integer.parseInt(serializedCipherCommandDataSections[3]);
        CipherPadding cipherPadding = CipherPadding.getPaddingById(cipherPaddingId);

        if (cipherPadding == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_REQUEST);

        int cipherKeySizeId = Integer.parseInt(serializedCipherCommandDataSections[4]);
        CipherKeySize cipherKeySize = CipherKeySize.getCipherKeySizeById(cipherKeySizeId);

        if (cipherKeySize == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_REQUEST);

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        cipherAlgorithm,
                        cipherMode,
                        cipherPadding,
                        cipherKeySize);

        if (cipherConfiguration == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_CIPHER_CONFIGURATION);

        long startTimeMilliseconds = Long.parseLong(serializedCipherCommandDataSections[5]);

        CipherCommandDataInitRequest cipherCommandDataInitRequest =
                CipherCommandDataInitRequest.getInstance(
                        cipherConfiguration,
                        startTimeMilliseconds);

        if (cipherCommandDataInitRequest == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_INIT_REQUEST_GENERATION);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRequest);

        return null;
    }

    public static Error parseCipherCommandDataInitAccept(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_ACCEPT_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_ACCEPT);

        CipherCommandDataInitAccept cipherCommandDataInitAccept =
                CipherCommandDataInitAccept.getInstance();

        if (cipherCommandDataInitAccept == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_INIT_ACCEPT_GENERATION);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitAccept);

        return null;
    }

    public static Error parseCipherCommandDataInitCompleted(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_COMPLETED_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_COMPLETED);

        String[] serializedPeerIdSideIdPairList =
                serializedCipherCommandDataSections[1].split(
                        String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

        if (serializedPeerIdSideIdPairList.length < C_MIN_PEER_ID_SIDE_ID_PAIR_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIRS_COUNT);

        HashMap<Long,Integer> peerIdSideIdHashMap = new HashMap<>();

        for (final String serializedPeerIdSideIdPair : serializedPeerIdSideIdPairList) {
            String[] serializedPeerIdSideIdPairParts =
                    serializedPeerIdSideIdPair.split(
                            String.valueOf(CommandContext.C_PAIR_DATA_DIVIDER_CHAR));

            if (serializedPeerIdSideIdPairParts.length < 2)
                return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_PARTS_COUNT);;

            long peerId = Long.parseLong(serializedPeerIdSideIdPairParts[0]);
            int sideId = Integer.parseInt(serializedPeerIdSideIdPairParts[1]);

            if (peerId == 0 || sideId < 0)
                return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_PEER_ID_SIDE_ID_PAIR_DATA);;

            peerIdSideIdHashMap.put(peerId, sideId);
        }

        byte[] publicKeyBytes =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[2].getBytes(StandardCharsets.UTF_8));

        if (publicKeyBytes == null)
            return C_ERROR_HASH_MAP.get(ErrorType.NULL_PUBLIC_KEY_BYTES);

        PublicKey publicKey =
                CipherKeyUtility.generatePublicKeyWithBytes(
                        CipherContext.C_ALGORITHM,
                        publicKeyBytes);

        if (publicKey == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_PUBLIC_KEY_CREATION);

        CipherCommandDataInitRequestCompleted cipherCommandDataInitRequestCompleted =
                CipherCommandDataInitRequestCompleted.getInstance(
                        peerIdSideIdHashMap,
                        publicKey);

        if (cipherCommandDataInitRequestCompleted == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_INIT_COMPLETED_GENERATION);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRequestCompleted);

        return null;
    }

    public static Error parseCipherCommandDataInitRoute(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_ROUTE_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_INIT_ROUTE);

        String[] routeIdDataPairs =
                serializedCipherCommandDataSections[1].split(
                        String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

        if (routeIdDataPairs.length <= 0)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIRS_COUNT);

        HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap = new HashMap<>();

        for (final String routeIdDataPair : routeIdDataPairs) {
            String[] routeIdDataPairParts =
                    routeIdDataPair.split(String.valueOf(CommandContext.C_PAIR_DATA_DIVIDER_CHAR));

            if (routeIdDataPairParts.length < 3)
                return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_PARTS_COUNT);

            int sideId = Integer.parseInt(routeIdDataPairParts[0]);
            int routeId = Integer.parseInt(routeIdDataPairParts[1]);
            byte[] data = Base64.getDecoder().decode(routeIdDataPairParts[2].getBytes(StandardCharsets.UTF_8));

            if (sideId < 0 || routeId < 0 || data == null)
                return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_ROUTE_ID_DATA_PAIR_DATA);;

            sideIdRouteIdDataHashMap.put(sideId, new Pair<>(routeId, data));
        }

        CipherCommandDataInitRoute cipherCommandDataInitRoute =
                CipherCommandDataInitRoute.getInstance(sideIdRouteIdDataHashMap);

        if (cipherCommandDataInitRoute == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_INIT_ROUTE_GENERATION);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRoute);

        return null;
    }

    public static Error parseCipherCommandDataSessionSet(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_SESSION_SET_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_SESSION_SET);

        CipherCommandDataSessionSet cipherCommandDataSessionSet =
                CipherCommandDataSessionSet.getInstance();

        if (cipherCommandDataSessionSet == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_SESSION_SET_GENERATION);

        cipherCommandDataWrapper.setValue(cipherCommandDataSessionSet);

        return null;
    }
}
