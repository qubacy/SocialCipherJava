package com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.serializer;

import android.util.Base64;
import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandContext;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.util.List;

public class CipherCommandDataSerializer {
    public static Error serializeCipherCommandData(
            final CipherCommandData cipherCommandData,
            ObjectWrapper<String> serializedCipherCommandDataWrapper)
    {
        if (cipherCommandData == null || serializedCipherCommandDataWrapper == null)
            return new Error("Cipher Command Data serialization args were incorrect!", true);

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
        List<Pair<Long,Integer>> peerIdSideIdPairList =
                cipherCommandData.getPeerIdSideIdPairList();
        int peerIdSideIdPairListSize = peerIdSideIdPairList.size();

        for (int i = 0; i < peerIdSideIdPairListSize; ++i) {
            StringBuilder peerIdSideIdString = new StringBuilder();
            Pair<Long, Integer> peerIdSideIdPair = peerIdSideIdPairList.get(i);

            peerIdSideIdString.
                    append(String.valueOf(peerIdSideIdPair.first))
                    .append(CommandContext.C_PAIR_DATA_DIVIDER_CHAR)
                    .append(String.valueOf(peerIdSideIdPair.second))
                    .append((i + 1 == peerIdSideIdPairListSize ?
                            "" :
                            CommandContext.C_SAME_TYPE_DATA_PIECE_DIVIDER_CHAR));

            serializedCipherCommandData.append(peerIdSideIdString.toString());
        }

        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        byte[] publicKeyBytes = cipherCommandData.getPublicKey().getEncoded();
        String publicKeyAsBase64String = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

        if (publicKeyAsBase64String == null)
            return new Error("Public Key encoding went wrong!", true);

        serializedCipherCommandData.append(publicKeyAsBase64String);
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        String sidePublicDataAsBase64String =
                Base64.encodeToString(cipherCommandData.getSidePublicData(), Base64.DEFAULT);

        if (sidePublicDataAsBase64String == null)
            return new Error("Side Public Data encoding went wrong!", true);

        serializedCipherCommandData.append(sidePublicDataAsBase64String);

        return null;
    }

    private static Error serializeRouteCipherCommandData(
            final CipherCommandDataInitRoute cipherCommandData,
            StringBuilder serializedCipherCommandData)
    {
        serializedCipherCommandData.append(String.valueOf(cipherCommandData.getRouteId()));
        serializedCipherCommandData.append(CommandContext.C_SECTION_DIVIDER_CHAR);

        String dataAsBase64String =
                Base64.encodeToString(cipherCommandData.getData(), Base64.DEFAULT);

        if (dataAsBase64String == null)
            return new Error("Routing Data encoding went wrong!", true);

        serializedCipherCommandData.append(dataAsBase64String);

        return null;
    }
}
