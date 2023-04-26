package com.mcdead.busycoder.socialcipher.cipher.processor.command;

import com.mcdead.busycoder.socialcipher.cipher.processor.command.data.CipherSessionPreInitData;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.command.data.entity.CommandData;
import com.mcdead.busycoder.socialcipher.command.processor.CommandProcessor;
import com.mcdead.busycoder.socialcipher.command.processor.data.CommandMessage;

import java.util.HashMap;

/*

    This class should get CipherSession and current CIPHER_ command,
    decide what to do next and
    modify CipherSession due to the current state;

    HOW to process ACCEPT commands with a certain TIMEOUT?

*/
public class CipherCommandProcessor implements CommandProcessor {
    final private HashMap<Long, CipherSessionPreInitData> m_chatIdPreInitDataHashMap;

    public CipherCommandProcessor() {
        m_chatIdPreInitDataHashMap = new HashMap<>();
    }

    @Override
    public Error processCommand(
            final CommandData commandData)
    {
        // todo: processing new command;



        return null;
    }

    @Override
    public void execState() {
        // todo: check if available time for establishing ciphering session
        // todo: has been expired;


    }


}
