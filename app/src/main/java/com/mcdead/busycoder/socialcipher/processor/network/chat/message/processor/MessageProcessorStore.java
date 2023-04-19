package com.mcdead.busycoder.socialcipher.processor.chat.message.processor;

public class MessageProcessorStore {
    private static MessageProcessorBase s_processor = null;

    public static MessageProcessorBase getProcessor() {
        return s_processor;
    }

    public static boolean init(MessageProcessorBase messageProcessor) {
        if (messageProcessor == null) return false;

        s_processor = messageProcessor;

        return true;
    }
}
