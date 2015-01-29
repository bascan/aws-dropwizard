package io.interact.sqsdw;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

public class MessageHandlerTest {

    private static final String TEST_TYPE = "TestType";

    @Test
    public void testConstructor() {
        try {
            new MessageHandler(null) {

                @Override
                public void handle(Message message) {
                }
            };
            fail("Constructor should not have accepted null");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void canHandleUnknownValue() {
        // Prepare message
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(MessageHandler.ATTR_MESSAGE_TYPE, new MessageAttributeValue().withDataType("String")
                .withStringValue("unknown-value"));
        Message message = new Message();
        message.setMessageAttributes(messageAttributes);

        // Prepare handler
        MessageHandler fixture = new MessageHandler(TEST_TYPE) {

            @Override
            public void handle(Message message) {
            }
        };

        // Verify
        assertFalse(fixture.canHandle(message));
    }

    @Test
    public void canHandle() {
        // Prepare message
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(MessageHandler.ATTR_MESSAGE_TYPE, new MessageAttributeValue().withDataType("String")
                .withStringValue(TEST_TYPE));
        Message message = new Message();
        message.setMessageAttributes(messageAttributes);

        // Prepare handler
        MessageHandler fixture = new MessageHandler(TEST_TYPE) {

            @Override
            public void handle(Message message) {
            }
        };

        // Verify
        assertTrue(fixture.canHandle(message));
        assertFalse(fixture.canHandle(new Message()));
        assertFalse(fixture.canHandle(null));
    }
}
