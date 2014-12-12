package io.interact.sqsdw;

import com.amazonaws.services.sqs.model.Message;

/**
 * Handles messages that were received by the {@link SqsListener}.
 * 
 * @author Bas Cancrinus
 */
public interface MessageHandler {

    /**
     * Called by the {@link SqsListener} for each {@link Message} that was
     * received.
     * 
     * @param message
     *            The message that was received by the {@link SqsListener}.
     */
    public void handle(Message message);
}
