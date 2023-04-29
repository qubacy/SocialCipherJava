package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import android.util.Pair;

import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKey;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.key.CipherKeyGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSession;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.CipherSessionGenerator;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.CipherSessionStateInit;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.init.data.CipherSessionInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state.set.CipherSessionStateSet;
import com.mcdead.busycoder.socialcipher.cipher.data.storage.CipherSessionStore;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererBase;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.CiphererGenerator;
import com.mcdead.busycoder.socialcipher.cipher.processor.cipherer.configuration.CipherConfiguration;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequest;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRequestCompleted;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitRoute;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataSessionSet;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer.CipherSessionInitBuffer;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer.CipherSessionPreInitData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.buffer.CipherSessionPreInitDataInitializer;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser.CipherCommandDataParser;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.serializer.CipherCommandDataSerializer;
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

            if (m_callback.getLocalPeerId() != chatIdPreInitDataEntry.getKey()) {
                dataToRemoveKeyList.add(chatIdPreInitDataEntry.getKey());

                return null;
            }

            List<Long> userPeerIdList =
                    ((CipherSessionPreInitDataInitializer)chatIdPreInitDataEntry.getValue()).
                            getUserPeerIdList();

            if (userPeerIdList.isEmpty()) {
                dataToRemoveKeyList.add(chatIdPreInitDataEntry.getKey());

                continue;
            }

            Error newSessionCreatingError =
                    createNewCipherSession(chatIdPreInitDataEntry.getKey(), userPeerIdList);

            if (newSessionCreatingError != null)
                return newSessionCreatingError;

            CipherSession cipherSession =
                    CipherSessionStore.
                            getInstance().
                            getSessionByChatId(chatIdPreInitDataEntry.getKey());
            CipherSessionStateInit cipherSessionStateInit =
                    (CipherSessionStateInit) cipherSession.getState();

            PublicKey publicKey = cipherSessionStateInit.getPublicKey();

            CipherCommandDataInitRequestCompleted cipherCommandDataInitRequestCompleted =
                    CipherCommandDataInitRequestCompleted.getInstance(
                            cipherSession.getUserPeerIdSessionSideIdHashMap(),
                            cipherSessionStateInit.getPublicKey(),
                            cipherSessionStateInit.processKeyData(publicKey.getEncoded(), false));

            ObjectWrapper<String> serializedCommandWrapper = new ObjectWrapper<>();
            Error serializationError =
                    CipherCommandDataSerializer.serializeCipherCommandData(
                            cipherCommandDataInitRequestCompleted,
                            serializedCommandWrapper);

            if (serializationError != null)
                return serializationError;

            List<Long> receiverPeerId = new ArrayList<>();

            m_callback.sendCommand(
                    CommandCategory.CIPHER,
                    chatIdPreInitDataEntry.getKey(),
                    receiverPeerId,
                    serializedCommandWrapper.getValue());

            //dataToRemoveKeyList.add(chatIdPreInitDataEntry.getKey());
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
            final HashMap<Long, Integer> peerIdSideIdHashMap)
    {
        try {
            KeyPair keyPair = CipherKeyUtility.generateKeyPairWithPublicKey(publicKey);
            KeyAgreement keyAgreement = CipherKeyUtility.generateKeyAgreement(keyPair.getPrivate());

            CipherSession cipherSession =
                    CipherSessionGenerator.generateCipherSessionWithSessionSideIdUserPeerIdPairList(
                            m_callback.getLocalPeerId(),
                            keyPair,
                            keyAgreement,
                            peerIdSideIdHashMap);

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
        if (m_chatIdPreInitDataHashMap.containsKey(chatId))
            return null;

        // todo: checking the provided configuration for availability..


        CipherConfiguration cipherConfiguration =
                CipherConfiguration.getInstance(
                        initRequestCommand.getCipherAlgorithm(),
                        initRequestCommand.getCipherMode(),
                        initRequestCommand.getCipherPadding(),
                        initRequestCommand.getCipherKeySize());

        if (cipherConfiguration == null) return null;

        CipherRequestAnswerSettingSession answer =
                m_callback.onCipherSessionSettingRequestReceived();

        if (!answer.getAnswer()) return null;

        List<Long> receiverPeerIdList = new ArrayList<>();

        receiverPeerIdList.add(initializerPeerId);

        m_callback.sendCommand(CommandCategory.CIPHER, chatId, receiverPeerIdList, "");

        // todo: adding new buffer obj.

        CipherSessionInitBuffer buffer =
                new CipherSessionInitBuffer(
                        cipherConfiguration,
                        initializerPeerId);

        CipherSessionPreInitData cipherSessionPreInitData =
                new CipherSessionPreInitData(
                        initRequestCommand.getStartTimeMilliseconds(),
                        buffer);

        m_chatIdPreInitDataHashMap.put(chatId, cipherSessionPreInitData);

        return null;
    }

    private Error processInitAcceptCommand(
            final CipherCommandDataInitAccept initAcceptCommand,
            final long chatId,
            final long initializerPeerId)
    {
        if (!m_chatIdPreInitDataHashMap.containsKey(chatId))
            return null; // todo: think of it;

        CipherSessionPreInitDataInitializer preInitData =
                (CipherSessionPreInitDataInitializer) m_chatIdPreInitDataHashMap.get(chatId);

        if (preInitData == null)
            return new Error(, true);

        preInitData.addUser(initializerPeerId);

        return null;
    }

    private Error processInitCompletedCommand(
            final CipherCommandDataInitRequestCompleted initRequestCompletedCommand,
            final long chatId)
    {
        if (!m_chatIdPreInitDataHashMap.containsKey(chatId))
            return null;

        // todo: creating new session obj. using provided data..

        Error cipherSessionCreatingError =
                createNewCipherSessionSlave(
                        chatId,
                        initRequestCompletedCommand.getPublicKey(),
                        initRequestCompletedCommand.getPeerIdSideIdHashMap());

        if (cipherSessionCreatingError != null)
            return cipherSessionCreatingError;

        // todo: process received 0-side-public data (HOW??)..

        CipherSessionStore cipherSessionStore = CipherSessionStore.getInstance();

        if (cipherSessionStore == null)
            return new Error(, true);

        CipherSession cipherSession = cipherSessionStore.getSessionByChatId(chatId);

        if (cipherSession == null) return null;

        Error processingRouteError = processRoute(
                cipherSession.getLocalSessionSideId() - 1,
                initRequestCompletedCommand.getSidePublicData(),
                chatId,
                cipherSession);

        if (processingRouteError != null)
            return processingRouteError;

        // todo: last side id route sending...

        if (cipherSession.getLocalSessionSideId() != cipherSession.getSessionSideCount() - 1)
            return null;

        CipherSessionStateInit cipherSessionStateInit =
                (CipherSessionStateInit) cipherSession.getState();

        byte[] publicSideData =
                cipherSessionStateInit.processKeyData(
                    cipherSessionStateInit.getPublicKey().getEncoded(),
                    false);
        int routeId = cipherSession.getSessionSideCount() - 1;
        CipherSessionInitRoute route =
                cipherSessionStateInit.getRouteById(routeId);
        int nextSideId = route.getNextSideId(cipherSession.getLocalSessionSideId());

        if (nextSideId < 0)
            return new Error(, );

        HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap =
                new HashMap<>();

        sideIdRouteIdDataHashMap.put(nextSideId, new Pair<>(routeId, publicSideData));

        CipherCommandDataInitRoute commandDataInitRoute =
                CipherCommandDataInitRoute.getInstance(sideIdRouteIdDataHashMap);

        if (commandDataInitRoute == null)
            return new Error(, true);

        List<Long> receiverPeerIdList = new ArrayList<>();

        ObjectWrapper<String> serializedCommandWrapper = new ObjectWrapper<>();
        Error serializedCommandError =
                CipherCommandDataSerializer.serializeCipherCommandData(
                        commandDataInitRoute,
                        serializedCommandWrapper);

        if (serializedCommandError != null)
            return serializedCommandError;

        m_callback.sendCommand(
                CommandCategory.CIPHER,
                chatId,
                receiverPeerIdList,
                serializedCommandWrapper.getValue());

        return null;
    }

    private Error processInitRouteCommand(
            final CipherCommandDataInitRoute initRouteCommand,
            final long chatId,
            final long initializerPeerId)
    {
        if (!m_chatIdPreInitDataHashMap.containsKey(chatId))
            return null;

        // todo: getting a session obj...

        CipherSessionStore cipherSessionStore = CipherSessionStore.getInstance();

        if (cipherSessionStore == null)
            return new Error(, true);

        CipherSession cipherSession = cipherSessionStore.getSessionByChatId(chatId);

        if (cipherSession == null) return null;

        HashMap<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMap =
                initRouteCommand.getSideIdRouteIdDataHashMap();
        Pair<Integer, byte[]> localRouteIdDataPair =
                sideIdRouteIdDataHashMap.get(cipherSession.getLocalSessionSideId());

        long chatInitializerPeerId =
                m_chatIdPreInitDataHashMap.get(chatId).getBuffer().getInitializerPeerId();

        if (localRouteIdDataPair != null) {
            Error processingRouteError =
                    processRoute(
                        localRouteIdDataPair.first,
                        localRouteIdDataPair.second,
                        chatId,
                        cipherSession);

            if (processingRouteError != null)
                return processingRouteError;
        }

        if (chatInitializerPeerId == m_callback.getLocalPeerId()) {
            // todo: processing by host..

            CipherSessionPreInitDataInitializer cipherSessionPreInitDataInitializer =
                    (CipherSessionPreInitDataInitializer) m_chatIdPreInitDataHashMap.get(chatId);

            if (cipherSessionPreInitDataInitializer == null)
                return new Error(, true);

            for (final Map.Entry<Integer, Pair<Integer, byte[]>> sideIdRouteIdDataHashMapEntry :
                    sideIdRouteIdDataHashMap.entrySet())
            {
                cipherSessionPreInitDataInitializer.
                        addRouteCounterValue(sideIdRouteIdDataHashMapEntry.getValue().first);
            }

            if (!cipherSessionPreInitDataInitializer.isRouteCounterListFull())
                return null;

            // todo: sending SESSION_SET command..

            List<Long> receiverUserPeerIdList = new ArrayList<>();

            CipherCommandDataSessionSet commandDataSessionSet =
                    CipherCommandDataSessionSet.getInstance();

            ObjectWrapper<String> serializedCommandDataSessionSet =
                    new ObjectWrapper<>();
            Error serializingCommandError =
                    CipherCommandDataSerializer.serializeCipherCommandData(
                            commandDataSessionSet,
                            serializedCommandDataSessionSet);

            if (serializingCommandError != null)
                return serializingCommandError;

            m_callback.sendCommand(
                    CommandCategory.CIPHER,
                    chatId,
                    receiverUserPeerIdList,
                    serializedCommandDataSessionSet.getValue());
        }

        return null;
    }

    private Error processRoute(
            final int routeId,
            final byte[] data,
            final long chatId,
            final CipherSession cipherSession)
    {
        CipherSessionStateInit cipherSessionStateInit =
                (CipherSessionStateInit) cipherSession.getState();

        CipherSessionInitRoute route =
                cipherSessionStateInit.getRouteById(routeId);

        if (route == null)
            return new Error(, true);

        int nextSideId = route.getNextSideId(cipherSession.getLocalSessionSideId());
        boolean isLastStage = false;

        if (nextSideId == CipherSessionInitRoute.C_NO_NEXT_SIDE_ID)
            isLastStage = true;

        byte[] processedData =
                cipherSessionStateInit.processKeyData(
                        data,
                        isLastStage);

        if (isLastStage)
            return processSetState(chatId, cipherSession, processedData);

        HashMap<Integer, Pair<Integer, byte[]>> nextSideIdRouteIdDataHashMap = new HashMap<>();

        nextSideIdRouteIdDataHashMap.put(
                nextSideId,
                new Pair<>(route.getNextSideId(cipherSession.getLocalSessionSideId()), processedData));

        CipherCommandDataInitRoute nextCipherCommandRoute =
                CipherCommandDataInitRoute.getInstance(nextSideIdRouteIdDataHashMap);

        if (nextCipherCommandRoute == null)
            return new Error(, true);

        List<Long> receiverPeerIdList = new ArrayList<>();

        ObjectWrapper<String> nextCipherCommandRouteString = new ObjectWrapper<>();
        Error commandSerializingError =
                CipherCommandDataSerializer.serializeCipherCommandData(
                        nextCipherCommandRoute,
                        nextCipherCommandRouteString);

        if (commandSerializingError != null)
            return commandSerializingError;

        m_callback.sendCommand(
                CommandCategory.CIPHER,
                chatId,
                receiverPeerIdList,
                nextCipherCommandRouteString.getValue());

        return null;
    }

    private Error processSetState(
            final long chatId,
            final CipherSession cipherSession,
            final byte[] sharedSecret)
    {
        CipherSessionPreInitData preInitData = m_chatIdPreInitDataHashMap.get(chatId);

        if (preInitData == null)
            return null;

        CipherSessionInitBuffer buffer = preInitData.getBuffer();

        if (buffer == null)
            return new Error(, true);

        CipherConfiguration cipherConfiguration = buffer.getCipherConfiguration();
        CipherKey cipherKey =
                CipherKeyGenerator.generateCipherKeyWithConfiguration(
                        cipherConfiguration, sharedSecret);

        if (cipherKey == null)
            return new Error(, true);

        CiphererBase cipherer =
                CiphererGenerator.generateCiphererWithConfiguration(cipherConfiguration, cipherKey);

        if (cipherer == null)
            return new Error(, true);

        CipherSessionStateSet cipherSessionStateSet =
                CipherSessionStateSet.getInstance(cipherer);

        if (!cipherSession.setSessionState(cipherSessionStateSet))
            return new Error(, true);

        return null;
    }

    private Error processSessionSetCommand(
            final CipherCommandDataSessionSet sessionSetCommand)
    {
        // todo: notifying a local user about successful session setting..


    }
}
