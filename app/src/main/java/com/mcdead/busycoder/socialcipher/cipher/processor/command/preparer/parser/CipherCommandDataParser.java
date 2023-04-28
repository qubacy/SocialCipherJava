package com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.CipherContext;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherAlgorithm;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherMode;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherPadding;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.CipherCommandType;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.utility.CipherKeyUtility;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CipherCommandDataParser {
    private static final int C_SHARED_SECTION_COUNT = 1;
    private static final int C_MIN_SECTION_COUNT = 2;

    private static final int C_INIT_REQUEST_SECTION_COUNT = 3;
    private static final int C_INIT_ACCEPT_SECTION_COUNT = 0;
    private static final int C_INIT_COMPLETED_SECTION_COUNT = 3;
    private static final int C_INIT_ROUTE_SECTION_COUNT = 2;

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
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided", true);

        int cipherAlgorithmId = Integer.parseInt(serializedCipherCommandDataSections[1]);
        CipherAlgorithm cipherAlgorithm = CipherAlgorithm.getAlgorithmById(cipherAlgorithmId);

        if (cipherAlgorithm == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided", true);

        int cipherModeId = Integer.parseInt(serializedCipherCommandDataSections[2]);
        CipherMode cipherMode = CipherMode.getModeById(cipherModeId);

        if (cipherMode == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided", true);

        int cipherPaddingId = Integer.parseInt(serializedCipherCommandDataSections[3]);
        CipherPadding cipherPadding = CipherPadding.getPaddingById(cipherPaddingId);

        if (cipherPadding == null)
            return new Error("Incorrect Serialized Cipher Command Data Init Request has been provided", true);

        CipherCommandDataInitRequest cipherCommandDataInitRequest =
                CipherCommandDataInitRequest.getInstance(cipherAlgorithm, cipherMode, cipherPadding);

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

        List<Pair<Long,Integer>> peerIdSideIdPairList = new ArrayList<>();

        for (final String serializedPeerIdSideIdPair : serializedPeerIdSideIdPairList) {
            String[] serializedPeerIdSideIdPairParts =
                    serializedPeerIdSideIdPair.split(
                            String.valueOf(CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            if (serializedPeerIdSideIdPairParts.length < 2)
                return new Error("Peer Id Side Id pair parts count was incorrect during parsing process!", true);

            long peerId = Long.parseLong(serializedPeerIdSideIdPairParts[0]);
            int sideId = Integer.parseInt(serializedPeerIdSideIdPairParts[1]);

            if (peerId == 0 || sideId < 0)
                return new Error("Peer Id Side Id were incorrect during parsing process!", true);

            peerIdSideIdPairList.add(new Pair<>(peerId, sideId));
        }

        byte[] publicKeyBytes =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[2]);

        if (publicKeyBytes == null)
            return new Error("Public Key Bytes were null during parsing process!", true);

        PublicKey publicKey =
                CipherKeyUtility.generatePublicKeyWithBytes(
                        CipherContext.C_ALGORITHM,
                        publicKeyBytes);

        if (publicKey == null)
            return new Error("Public Key creation failed during parsing process!", true);

        byte[] sidePublicData =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[3]);

        if (sidePublicData == null)
            return new Error("Side Public Data was null during parsing process!", true);

        CipherCommandDataInitRequestCompleted cipherCommandDataInitRequestCompleted =
                CipherCommandDataInitRequestCompleted.getInstance(
                        peerIdSideIdPairList,
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

        int routeId = Integer.parseInt(serializedCipherCommandDataSections[1]);

        if (routeId < 0)
            return new Error("Incorrect Serialized Cipher Command Data Init Route has been provided", true);

        byte[] data =
                Base64.getDecoder().decode(serializedCipherCommandDataSections[2]);

        if (data == null)
            return new Error("Data was null during parsing process!", true);

        CipherCommandDataInitRoute cipherCommandDataInitRoute =
                CipherCommandDataInitRoute.getInstance(routeId, data);

        if (cipherCommandDataInitRoute == null)
            return new Error("Cipher Command Data Init Route parsing has been failed!", true);

        cipherCommandDataWrapper.setValue(cipherCommandDataInitRoute);

        return null;
    }
}
