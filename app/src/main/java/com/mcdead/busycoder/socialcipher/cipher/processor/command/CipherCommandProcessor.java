package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherCommandDataInitAccept;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherSessionPreInitData;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.preparer.parser.CipherCommandDataParser;
import com.mcdead.busycoder.socialcipher.cipher.processor.command.request.data.CipherRequestAnswerSettingSession;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.CommandProcessor;

import java.util.HashMap;

/*

    This class should get CipherSession and current CIPHER_ command,
    decide what to do next and
    modify CipherSession due to the current state;

    HOW to process ACCEPT commands with a certain TIMEOUT?

*/
public class CipherCommandProcessor implements CommandProcessor {
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
            final CommandData commandData)
    {
        if (commandData == null)
            return new Error("Provided Ciphering Command data was null!", true);

        CipherCommandData cipherCommandData =
                CipherCommandDataParser.parseCipherCommandData(
                        commandData.getSpecificCommandTypeData());

        if (cipherCommandData == null)
            return new Error("Cipher Command Data parsing went wrong!", true);

        switch (cipherCommandData.getType()) {
            case CIPHER_SESSION_INIT_REQUEST:
                return processInitRequestCommand((CipherCommandDataInitAccept) cipherCommandData);
            case CIPHER_SESSION_INIT_ACCEPT:
                return processInitAcceptCommand((CipherCommandDataInitAccept) cipherCommandData);
            case CIPHER_SESSION_INIT_ROUTE:
                return processInitRouteCommand((CipherCommandDataInitAccept) cipherCommandData);
            case CIPHER_SESSION_INIT_COMPLETED:
                return processInitCompletedCommand((CipherCommandDataInitAccept) cipherCommandData);
        }

        return new Error("Unknown type of Cipher Command Data has been provided!", true);
    }

    @Override
    public void execState() {
        // todo: check if available time for establishing ciphering session
        // todo: has been expired;


    }

    private Error processInitRequestCommand(
            final CipherCommandDataInitAccept initAcceptCommand)
    {
        // todo: asking user for participating in a new cipher session..

        CipherRequestAnswerSettingSession answer =
                m_callback.onCipherSessionSettingRequestReceived();


    }

    private Error processInitAcceptCommand(
            final CipherCommandDataInitAccept initAcceptCommand)
    {

    }

    private Error processInitRouteCommand(
            final CipherCommandDataInitAccept initAcceptCommand)
    {

    }

    private Error processInitCompletedCommand(
            final CipherCommandDataInitAccept initAcceptCommand)
    {

    }
}
