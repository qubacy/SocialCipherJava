package com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.serializer;

import android.util.Base64;
import androidx.core.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataSessionSet;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.HashMap;
import java.util.Map;

public class CipherCommandDataSerializer {
    public static final HashMap<ErrorType, Error> C_ERROR_HASH_MAP =
            new HashMap<ErrorType, Error>()
            {
                {
                    put(ErrorType.INCORRECT_ARGS,
                            new Error("Cipher Command Data serialization args were incorrect!", true));
                    put(ErrorType.FAILED_PUBLIC_KEY_ENCODING,
                            new Error("Public Key encoding went wrong!", true));
                    put(ErrorType.FAILED_ROUTE_DATA_ENCODING,
                            new Error("Routing Data encoding went wrong!", true));
                }
            };

    public static enum ErrorType {
        INCORRECT_ARGS,

        FAILED_PUBLIC_KEY_ENCODING,
        FAILED_ROUTE_DATA_ENCODING,

    };

    public static Error serializeCipherCommandData(
            final CipherCommandData cipherCommandData,
            ObjectWrapper<String> serializedCipherCommandDataWrapper)
    {
        if (cipherCommandData == null || serializedCipherCommandDataWrapper == null)
            return C_ERROR_HASH_MAP.get(ErrorType.INCORRECT_ARGS);

        StringBuilder serializedCipherCommandData = new StringBuilder();

        serializedCipherCommandData.append(String.valueOf(cipherCommandData.getType().getId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        Error serializingError = null;

        switch (cipherCommandData.getType()) {
            case CIPHER_SESSION_INIT_REQUEST: {
                serializingError =
                        serializeInitRequestCipherCommandData(
                                (CipherCommandDataInitRequest) cipherCommandData,
                                serializedCipherCommandData);
                break;
            }
            case CIPHER_SESSION_INIT_ACCEPT: {
                serializingError =
                        serializeInitAcceptCipherCommandData(
                                (CipherCommandDataInitAccept) cipherCommandData,
                                serializedCipherCommandData);
                break;
            }
            case CIPHER_SESSION_INIT_COMPLETED: {
                serializingError =
                        serializeInitCompletedCipherCommandData(
                                (CipherCommandDataInitRequestCompleted) cipherCommandData,
                                serializedCipherCommandData);
                break;
            }
            case CIPHER_SESSION_INIT_ROUTE: {
                serializingError =
                        serializeRouteCipherCommandData(
                                (CipherCommandDataInitRoute) cipherCommandData,
                                serializedCipherCommandData);
                break;
            }
            case CIPHER_SESSION_SET: {
                serializingError =
                        serializeSessionSetCipherCommandData(
                                (CipherCommandDataSessionSet) cipherCommandData,
                                serializedCipherCommandData);
                break;
            }
        }

        if (serializingError != null)
            return serializingError;

        serializedCipherCommandDataWrapper.setValue(serializedCipherCommandData.toString());

        return null;
    }

    private static Error serializeInitRequestCipherCommandData(
            final CipherCommandDataInitRequest cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        serializedCipherCommandData.append(
                String.valueOf(cipherCommandData.getCipherAlgorithm().getId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);
        serializedCipherCommandData.append(
                String.valueOf(cipherCommandData.getCipherMode().getId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);
        serializedCipherCommandData.append(
                String.valueOf(cipherCommandData.getCipherPadding().getId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);
        serializedCipherCommandData.append(
                String.valueOf(cipherCommandData.getCipherKeySize().getId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);
        serializedCipherCommandData.append(
                String.valueOf(cipherCommandData.getStartTimeMilliseconds()));


        return null;
    }

    private static Error serializeInitAcceptCipherCommandData(
            final CipherCommandDataInitAccept cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        return null;
    }

    private static Error serializeInitCompletedCipherCommandData(
            final CipherCommandDataInitRequestCompleted cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        HashMap<Long,Integer> peerIdSideIdHashMap =
                cipherCommandData.getPeerIdSideIdHashMap();
        int peerIdSideIdPairListSize = peerIdSideIdHashMap.size();

        int index = 0;

        for (final Map.Entry<Long, Integer> peerIdSideIdEntry : peerIdSideIdHashMap.entrySet()) {
            StringBuilder peerIdSideIdString = new StringBuilder();

            peerIdSideIdString.
                    append(String.valueOf(peerIdSideIdEntry.getKey()))
                    .append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR)
                    .append(String.valueOf(peerIdSideIdEntry.getValue()))
                    .append((index + 1 == peerIdSideIdPairListSize ?
                            "" :
                            CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            serializedCipherCommandData.append(peerIdSideIdString.toString());

            ++index;
        }

        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        byte[] publicKeyBytes = cipherCommandData.getPublicKey().getEncoded();
        String publicKeyAsBase64String = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP);

        if (publicKeyAsBase64String == null)
            return C_ERROR_HASH_MAP.get(ErrorType.FAILED_PUBLIC_KEY_ENCODING);

        serializedCipherCommandData.append(publicKeyAsBase64String);

        return null;
    }

    private static Error serializeRouteCipherCommandData(
            final CipherCommandDataInitRoute cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap =
                cipherCommandData.getSideIdRouteIdDataHashMap();

        int sideIdRouteIdDataHashMapSize = sideIdRouteIdDataHashMap.size();
        int index = 0;

        for (final Map.Entry<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataEntry :
                sideIdRouteIdDataHashMap.entrySet())
        {
            serializedCipherCommandData.append(String.valueOf(sideIdRouteIdDataEntry.getKey()));
            serializedCipherCommandData.append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR);
            serializedCipherCommandData.append(String.valueOf(sideIdRouteIdDataEntry.getValue().first));
            serializedCipherCommandData.append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR);

            String dataAsBase64String =
                    Base64.encodeToString(
                            (byte[]) sideIdRouteIdDataEntry.getValue().second, Base64.NO_WRAP);

            if (dataAsBase64String == null)
                return C_ERROR_HASH_MAP.get(ErrorType.FAILED_ROUTE_DATA_ENCODING);

            serializedCipherCommandData.append(dataAsBase64String);
            serializedCipherCommandData.append((index + 1 ==  sideIdRouteIdDataHashMapSize ?
                    "" :
                    CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            ++index;
        }

        return null;
    }

    private static Error serializeSessionSetCipherCommandData(
            final CipherCommandDataSessionSet cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        return null;
    }
}
