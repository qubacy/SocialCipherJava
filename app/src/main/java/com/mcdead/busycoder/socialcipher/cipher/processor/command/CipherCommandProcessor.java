package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSessionGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.storage.CipherSessionStore;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataSessionSet;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherSessionPreInitData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser.CipherCommandDataParser;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.cipher.utility.CipherKeyUtility;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.CommandCategory;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.CommandProcessor;
import com.mcdead.busycoder.socialcipher.utility.ObjectWrapper;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyAgreement;

/*

    This class should get CipherSession and current CIPHER_ command,
    decide what to do next and
    modify CipherSession due to the current state;

    HOW to process ACCEPT commands with a certain TIMEOUT?

*/
public class CipherCommandProcessor implements CommandProcessor {
    private static final long C_SESSION_SETTING_PRE_INIT_PHASE_TIMESPAN_MILLISECONDS = 5000;

    final private HashMap<Long, CipherSessionPreInitData> m_chatIdPreInitDataHashMap;
    private boolean m_isAcceptedSession;

    final private CipherCommandProcessorCallback m_callback;

    public CipherCommandProcessor(
            final CipherCommandProcessorCallback callback)
    {
        m_chatIdPreInitDataHashMap = new HashMap<>();

        m_callback = callback;
    }

    @Override
    public Error processCommand(
            final CommandData commandData,
            final long initializerPeerId)
    {
        if (commandData == null)
            return new Error("Provided Ciphering Command data was null!", true);

        ObjectWrapper<CipherCommandData> cipherCommandDataWrapper = new ObjectWrapper<>();
        Error commandParsingError =
                CipherCommandDataParser.parseCipherCommandData(
                        commandData.getSpecificCommandTypeData(),
                        cipherCommandDataWrapper);

        if (commandParsingError != null)
            return commandParsingError;

        CipherCommandData cipherCommandData = cipherCommandDataWrapper.getValue();

        switch (cipherCommandData.getType()) {
            case CIPHER_SESSION_INIT_REQUEST:
                return processInitRequestCommand(
                        (CipherCommandDataInitRequest) cipherCommandData,
                        commandData.getChatId(),
                        initializerPeerId);
            case CIPHER_SESSION_INIT_ACCEPT:
                return processInitAcceptCommand(
                        (CipherCommandDataInitAccept) cipherCommandData,
                        commandData.getChatId(),
                        initializerPeerId);
            case CIPHER_SESSION_INIT_ROUTE:
                return processInitRouteCommand(
                        (CipherCommandDataInitRoute) cipherCommandData,
                        commandData.getChatId(),
                        initializerPeerId);
            case CIPHER_SESSION_INIT_COMPLETED:
                return processInitCompletedCommand(
                        (CipherCommandDataInitRequestCompleted) cipherCommandData,
                        commandData.getChatId());
            case CIPHER_SESSION_SET:
                return processSessionSetCommand(
                        (CipherCommandDataSessionSet) cipherCommandData
                );
        }

        return new Error("Unknown type of Cipher Command Data has been provided!", true);
    }

    @Override
    public Error execState() {
        List<Long> dataToRemoveKeyList = new ArrayList<>();

        for (final Map.Entry<Long, CipherSessionPreInitData> chatIdPreInitDataEntry :
                m_chatIdPreInitDataHashMap.entrySet())
        {
            long curStartTime = chatIdPreInitDataEntry.getValue().getStartTime();

            if (curStartTime + C_SESSION_SETTING_PRE_INIT_PHASE_TIMESPAN_MILLISECONDS > System.currentTimeMillis())
                continue;

            List<Long> userPeerIdList = chatIdPreInitDataEntry.getValue().getUserPeerIdList();

            if (userPeerIdList.isEmpty())
                dataToRemoveKeyList.add(chatIdPreInitDataEntry.getKey());

            Error newSessionCreatingError =
                    createNewCipherSession(chatIdPreInitDataEntry.getKey(), userPeerIdList);

            if (newSessionCreatingError != null)
                return newSessionCreatingError;

            dataToRemoveKeyList.add(chatIdPreInitDataEntry.getKey());
        }

        for (final Long dataToRemoveKey : dataToRemoveKeyList) {
            m_chatIdPreInitDataHashMap.remove(dataToRemoveKey);
        }

        return null;
    }

    private Error createNewCipherSession(
            final long chatId,
            final List<Long> userPeerIdList)
    {
        try {
            KeyPair keyPair = CipherKeyUtility.generateKeyPair();
            KeyAgreement keyAgreement = CipherKeyUtility.generateKeyAgreement(keyPair.getPrivate());

            CipherSession cipherSession =
                    CipherSessionGenerator.generateCipherSession(
                            m_callback.getLocalPeerId(),
                            keyPair,
                            keyAgreement,
                            userPeerIdList);

            if (cipherSession == null)
                return new Error(, true);

            CipherSessionStore cipherSessionStore = CipherSessionStore.getInstance();

            if (cipherSessionStore == null)
                return new Error(, true);

            if (!cipherSessionStore.addSession(chatId, cipherSession))
                return new Error(, true);

        } catch (Throwable e) {
            e.printStackTrace();

            return new Error(, true);
        }

        return null;
    }

    private Error createNewCipherSessionSlave(
            final long chatId,
            final PublicKey publicKey,
            final List<Pair<Long, Integer>> peerIdSideIdPairList)
    {
        try {
            KeyPair keyPair = CipherKeyUtility.generateKeyPairWithPublicKey(publicKey);
            KeyAgreement keyAgreement = CipherKeyUtility.generateKeyAgreement(keyPair.getPrivate());

            CipherSession cipherSession =
                    CipherSessionGenerator.generateCipherSessionWithSessionSideIdUserPeerIdPairList(
                            m_callback.getLocalPeerId(),
                            keyPair,
                            keyAgreement,
                            peerIdSideIdPairList);

            if (cipherSession == null)
                return new Error(, true);

            CipherSessionStore cipherSessionStore = CipherSessionStore.getInstance();

            if (cipherSessionStore == null)
                return new Error(, true);

            if (!cipherSessionStore.addSession(chatId, cipherSession))
                return new Error(, true);

        } catch (Throwable e) {
            e.printStackTrace();

            return new Error(, true);
        }

        return null;
    }

    private Error processInitRequestCommand(
            final CipherCommandDataInitRequest initRequestCommand,
            final long chatId,
            final long initializerPeerId)
    {
        // todo: checking the provided configuration for availability..



        CipherRequestAnswerSettingSession answer =
                m_callback.onCipherSessionSettingRequestReceived();

        if (!answer.getAnswer()) return null;

        List<Long> receiverPeerIdList = new ArrayList<>();

        receiverPeerIdList.add(initializerPeerId);

        m_callback.sendCommand(CommandCategory.CIPHER, chatId, receiverPeerIdList, "");

        m_isAcceptedSession = true;

        return null;
    }

    private Error processInitAcceptCommand(
            final CipherCommandDataInitAccept initAcceptCommand,
            final long chatId,
            final long initializerPeerId)
    {
        if (!m_chatIdPreInitDataHashMap.containsKey(chatId))
            return null; // todo: think of it;

        CipherSessionPreInitData preInitData = m_chatIdPreInitDataHashMap.get(chatId);

        if (preInitData == null)
            return new Error(, true);

        preInitData.addUser(initializerPeerId);

        return null;
    }

    private Error processInitCompletedCommand(
            final CipherCommandDataInitRequestCompleted initRequestCompletedCommand,
            final long chatId)
    {
        // todo: creating new session obj. using provided data..

        Error cipherSessionCreatingError =
                createNewCipherSessionSlave(
                        chatId,
                        initRequestCompletedCommand.getPublicKey(),
                        initRequestCompletedCommand.getPeerIdSideIdPairList());

        if (cipherSessionCreatingError != null)
            return cipherSessionCreatingError;

        // todo: process received 0-side-public data..



        return null;
    }

    private Error processInitRouteCommand(
            final CipherCommandDataInitRoute initRouteCommand,
            final long chatId,
            final long initializerPeerId)
    {
        // todo: getting a session obj...



        // todo: getting a routing (table?)..



        // todo: processing incoming data with a local secret..



        // todo: sending the result to a next Side..



    }

    private Error processSessionSetCommand(
            final CipherCommandDataSessionSet sessionSetCommand)
    {
        // todo: notifying a local user about successful session setting..


    }
}
