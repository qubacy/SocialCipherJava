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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class CipherCommandDataParser {
    private static final int C_SHARED_SECTION_COUNT = 1;
    private static final int C_MIN_SECTION_COUNT = 1;

    private static final int C_INIT_REQUEST_SECTION_COUNT = 5;
    private static final int C_INIT_ACCEPT_SECTION_COUNT = 0;
    private static final int C_INIT_COMPLETED_SECTION_COUNT = 3;
    private static final int C_INIT_ROUTE_SECTION_COUNT = 1;
    private static final int C_SESSION_SET_SECTION_COUNT = 0;

    private static final int C_MIN_PEER_ID_SIDE_ID_PAIR_COUNT = 2;

    public static Error parseCipherCommandData(
            final String cipherCommandDataString,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (cipherCommandDataString == null || cipherCommandDataWrapper == null)
            return new Error("Cipher Command Data parsing args were incorrect!", true);
        if (cipherCommandDataString.isEmpty())
            return new Error("Cipher Command Data string was empty!", true);

        String[] cipherCommandSections =
                cipherCommandDataString.split(
                        String.valueOf(CommandContext.C_SECTION_DIVIDER_CHAR));

        if (cipherCommandSections.length < C_MIN_SECTION_COUNT)
            return new Error("Serialized Cipher Command hadn't enough sections!", true);

        int cipherCommandTypeId = Integer.parseInt(cipherCommandSections[0]);
        CipherCommandType cipherCommandType =
                CipherCommandType.getCommandTypeById(cipherCommandTypeId);

        if (cipherCommandType == null)
            return new Error("Serialized Cipher Command had an incorrect type!", true);

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
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true);

        int cipherAlgorithmId = Integer.parseInt(serializedCipherCommandDataSections[1]);
        CipherAlgorithm cipherAlgorithm = CipherAlgorithm.getAlgorithmById(cipherAlgorithmId);

        if (cipherAlgorithm == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true);

        int cipherModeId = Integer.parseInt(serializedCipherCommandDataSections[2]);
        CipherMode cipherMode = CipherMode.getModeById(cipherModeId);

        if (cipherMode == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true);

        int cipherPaddingId = Integer.parseInt(serializedCipherCommandDataSections[3]);
        CipherPadding cipherPadding = CipherPadding.getPaddingById(cipherPaddingId);

        if (cipherPadding == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true);

        int cipherKeySizeId = Integer.parseInt(serializedCipherCommandDataSections[4]);
        CipherKeySize cipherKeySize = CipherKeySize.getCipherKeySizeById(cipherKeySizeId);

        if (cipherKeySize == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided!", true);

        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        cipherAlgorithm,
                        cipherMode,
                        cipherPadding,
                        cipherKeySize);

        if (cipherConfiguration == null)
            return new Error("Incorrect Cipher Configuration has been provided!", true);

        long startTimeMilliseconds = Long.parseLong(serializedCipherCommandDataSections[5]);

        CipherCommandDataInitRequest cipherCommandDataInitRequest =
                CipherCommandDataInitRequest.getInstance(
                        cipherConfiguration,
                        startTimeMilliseconds);

        if (cipherCommandDataInitRequest == null)
            return new Error("Cipher Command Data Init Request parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRequest);

        return null;
    }

    public static Error parseCipherCommandDataInitAccept(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_ACCEPT_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return new Error("Incorrect Serialized Cipher Command Data Init Accept has been provided", true);

        CipherCommandDataInitAccept cipherCommandDataInitAccept =
                CipherCommandDataInitAccept.getInstance();

        if (cipherCommandDataInitAccept == null)
            return new Error("Cipher Command Data Init Accept parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitAccept);

        return null;
    }

    public static Error parseCipherCommandDataInitCompleted(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_COMPLETED_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return new Error("Incorrect Serialized Cipher Command Data Init Completed has been provided", true);

        String[] serializedPeerIdSideIdPairList =
                serializedCipherCommandDataSections[1].split(
                        String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

        if (serializedPeerIdSideIdPairList.length < C_MIN_PEER_ID_SIDE_ID_PAIR_COUNT)
            return new Error("Peer Id Side Id pairs count was incorrect during parsing process!", true);

        HashMap<Long,Integer> peerIdSideIdHashMap = new HashMap<>();

        for (final String serializedPeerIdSideIdPair : serializedPeerIdSideIdPairList) {
            String[] serializedPeerIdSideIdPairParts =
                    serializedPeerIdSideIdPair.split(
                            String.valueOf(CommandContext.C_PAIR_DATA_DIVIDER_CHAR));

            if (serializedPeerIdSideIdPairParts.length < 2)
                return new Error("Peer Id Side Id pair parts count was incorrect during parsing process!", true);

            long peerId = Long.parseLong(serializedPeerIdSideIdPairParts[0]);
            int sideId = Integer.parseInt(serializedPeerIdSideIdPairParts[1]);

            if (peerId == 0 || sideId < 0)
                return new Error("Peer Id Side Id were incorrect during parsing process!", true);

            peerIdSideIdHashMap.put(peerId, sideId);
        }

        byte[] publicKeyBytes =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[2].getBytes(StandardCharsets.UTF_8));

        if (publicKeyBytes == null)
            return new Error("Public Key Bytes were null during parsing process!", true);

        PublicKey publicKey =
                CipherKeyUtility.generatePublicKeyWithBytes(
                        CipherContext.C_ALGORITHM,
                        publicKeyBytes);

        if (publicKey == null)
            return new Error("Public Key creation failed during parsing process!", true);

        byte[] sidePublicData =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[3].getBytes(StandardCharsets.UTF_8));

        if (sidePublicData == null)
            return new Error("Side Public Data was null during parsing process!", true);

        CipherCommandDataInitRequestCompleted cipherCommandDataInitRequestCompleted =
                CipherCommandDataInitRequestCompleted.getInstance(
                        peerIdSideIdHashMap,
                        publicKey,
                        sidePublicData);

        if (cipherCommandDataInitRequestCompleted == null)
            return new Error("Cipher Command Data Init Request Completed parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRequestCompleted);

        return null;
    }

    public static Error parseCipherCommandDataInitRoute(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_INIT_ROUTE_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return new Error("Incorrect Serialized Cipher Command Data Init Route has been provided", true);

        String[] routeIdDataPairs =
                serializedCipherCommandDataSections[1].split(
                        String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

        if (routeIdDataPairs.length <= 0)
            return new Error("Incorrect Serialized Cipher Command Data Init Route has been provided!", true);

        HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap = new HashMap<>();

        for (final String routeIdDataPair : routeIdDataPairs) {
            String[] routeIdDataPairParts =
                    routeIdDataPair.split(String.valueOf(CommandContext.C_PAIR_DATA_DIVIDER_CHAR));

            if (routeIdDataPairParts.length < 3)
                return new Error("Route Id Data pair parts count was incorrect during parsing process!", true);

            int sideId = Integer.parseInt(routeIdDataPairParts[0]);
            int routeId = Integer.parseInt(routeIdDataPairParts[1]);
            byte[] data = Base64.getDecoder().decode(routeIdDataPairParts[2]);

            if (sideId < 0 || routeId < 0 || data == null)
                return new Error("Route Id Data were incorrect during parsing process!", true);

            sideIdRouteIdDataHashMap.put(sideId, new Pair<>(routeId, data));
        }

        CipherCommandDataInitRoute cipherCommandDataInitRoute =
                CipherCommandDataInitRoute.getInstance(sideIdRouteIdDataHashMap);

        if (cipherCommandDataInitRoute == null)
            return new Error("Cipher Command Data Init Route parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRoute);

        return null;
    }

    public static Error parseCipherCommandDataSessionSet(
            final String[] serializedCipherCommandDataSections,
            ObjectWrapper<CipherCommandData> cipherCommandDataWrapper)
    {
        if (serializedCipherCommandDataSections.length < C_SESSION_SET_SECTION_COUNT + C_SHARED_SECTION_COUNT)
            return new Error("Incorrect Serialized Cipher Command Data Session Set has been provided", true);

        CipherCommandDataSessionSet cipherCommandDataSessionSet =
                CipherCommandDataSessionSet.getInstance();

        if (cipherCommandDataSessionSet == null)
            return new Error("Cipher Command Data Session Set parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataSessionSet);

        return null;
    }
}
