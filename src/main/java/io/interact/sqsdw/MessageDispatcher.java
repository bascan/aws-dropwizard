package io.interact.sqsdw;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Helps clients to dispatch messages to SQS, that can be handled by a matching
 * {@link MessageHandler}.
 * 
 * @author Bas Cancrinus
 */
public class MessageDispatcher {

    /**
     * Dispatches a message to SQS. {@link MessageHandler}s will handle the
     * message based on a matching value of messageType.
     * 
     * @param messageBody
     *            The body of the message.
     * @param queueUrl
     *            The SQS queue URL.
     * @param messageType
     *            The messageType.
     * @param sqs
     *            The SQS client.
     */
    public static void dispatch(String messageBody, String queueUrl, String messageType, AmazonSQS sqs) {
        dispatchDelayed(messageBody, queueUrl, messageType, sqs, 0);
    }

    public static void dispatchDelayed(String messageBody, String queueUrl, String messageType, AmazonSQS sqs, int delaySeconds) {
        sendMessage(messageBody, queueUrl, prepareMessageAttributes(messageType), sqs, delaySeconds);
    }

    private static Map<String, MessageAttributeValue> prepareMessageAttributes(String messageType) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(MessageHandler.ATTR_MESSAGE_TYPE,
                new MessageAttributeValue().withDataType("String").withStringValue(messageType));
        return messageAttributes;
    }

    private static void sendMessage(String messageBody, String queueUrl, Map<String, MessageAttributeValue> messageAttributes,
            AmazonSQS sqs, int delaySeconds) {
        SendMessageRequest request = new SendMessageRequest();
        request.withMessageBody(messageBody);
        request.withQueueUrl(queueUrl);
        request.withMessageAttributes(messageAttributes);
        request.setDelaySeconds(delaySeconds);
        sqs.sendMessage(request);
    }

}
