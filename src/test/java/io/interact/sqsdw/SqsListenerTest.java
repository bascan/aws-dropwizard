package io.interact.sqsdw;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

/**
 * Tests {@link SqsListener} lifecycle scenario's.
 * 
 * @author Bas Cancrinus
 */
public class SqsListenerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SqsListenerTest.class);

    private static final String TEST_QUEUE_URL = "test-queue-url";

    @Mock
    private AmazonSQS sqs;

    @Mock
    private MessageHandler handler;

    private SqsListener fixture;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fixture = new SqsListener(sqs, TEST_QUEUE_URL, handler);
    }

    @Test
    public void testLifecycleHealthy() throws Exception {
        LOG.debug("testLifecycleHealthy()...");
        fixture.start();
        assertTrue(fixture.isHealthy());
        fixture.stop();
    }

    @Test
    public void testLifecycleUnhealthy() throws Exception {
        LOG.debug("testLifecycleUnhealthy()...");

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(TEST_QUEUE_URL);
        when(sqs.receiveMessage(receiveMessageRequest)).thenThrow(new AmazonClientException(TEST_QUEUE_URL));

        fixture.start();
        Thread.sleep(500);
        assertFalse(fixture.isHealthy());
        fixture.stop();
    }

    @Test
    public void testLifecycleHealthyWithMessages() throws Exception {
        LOG.debug("testLifecycleHealthyWithMessages()...");

        List<Message> messages = new ArrayList<>();
        messages.add(new Message());
        messages.add(new Message());
        ReceiveMessageRequest request = new ReceiveMessageRequest(TEST_QUEUE_URL);
        ReceiveMessageResult result = new ReceiveMessageResult();
        result.setMessages(messages);

        when(sqs.receiveMessage(request)).thenReturn(result);

        fixture.start();
        Thread.sleep(500);
        assertTrue(fixture.isHealthy());
        fixture.stop();
    }
}