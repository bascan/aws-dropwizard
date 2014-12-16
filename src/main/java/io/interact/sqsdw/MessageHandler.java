package io.interact.sqsdw;

import com.amazonaws.services.sqs.model.Message;

/**
 * Handles messages that were received by the {@link SqsListenerImpl}.
 * 
 * @author Bas Cancrinus
 */
public interface MessageHandler {

    /**
     * Message attribute name that identifies the message type. The value can be
     * used by a {@link MessageHandler} implementation to determine whether the
     * message can be handled or not.
     * <p>
     * Example:<br/>
     * message.getMessageAttributes().get(MESSAGE_TYPE).getStringValue();
     * </p>
     */
    String MESSAGE_TYPE = "MessageType";

    /**
     * Called by the {@link SqsListenerImpl} for each {@link Message} that was
     * received.
     * 
     * @param message
     *            The message that was received by the {@link SqsListenerImpl}.
     */
    public void handle(Message message);
}
