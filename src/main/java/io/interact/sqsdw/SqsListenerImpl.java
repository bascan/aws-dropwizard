package io.interact.sqsdw;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Listens to a queue and dispatches received messages to the supplied
 * {@link MessageHandler} implementation.
 * 
 * @see SqsFactory
 * @see SqsListenerHealthCheck
 * @author Bas Cancrinus
 */
public class SqsListenerImpl implements SqsListener {

    private static final int SLEEP_ON_ERROR = 5000;

    private static final Logger LOG = LoggerFactory.getLogger(SqsListenerImpl.class);

    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AmazonSQS sqs;
    private final String sqsListenQueueUrl;
    private final MessageHandler handler;
    private final String interruptedMsg;

    private Thread pollingThread;

    /**
     * @param sqs
     *            Managed {@link AmazonSQS} instance that this listener will use
     *            to connect to its queue.
     * @param sqsListenQueueUrl
     *            URL of the queue where this instance will listen to (
     *            {@link Named} sqsListenQueueUrl).
     * @param handler
     *            Will be called for every message that this instance receives.
     */
    @Inject
    public SqsListenerImpl(AmazonSQS sqs, @Named("sqsListenQueueUrl") String sqsListenQueueUrl, MessageHandler handler) {
        this.sqs = sqs;
        this.sqsListenQueueUrl = sqsListenQueueUrl;
        this.handler = handler;

        interruptedMsg = "Stop listening to queue: " + sqsListenQueueUrl;
    }

    @Override
    public void start() throws Exception {
        pollingThread = new Thread() {

            @Override
            public void run() {

                LOG.info("Start listening to queue: " + sqsListenQueueUrl);
                while (!isInterrupted()) {
                    try {
                        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsListenQueueUrl);
                        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
                        for (int i = 0; i < messages.size(); i++) {
                            Message msg = messages.get(i);
                            LOG.debug(String.format("Processing message %s of %s...", i + 1, messages.size()));
                            try {
                                handler.handle(msg);
                            } catch (Exception e) {
                                logProcessingError(msg, e);
                            }

                            String messageRecieptHandle = msg.getReceiptHandle();
                            sqs.deleteMessage(new DeleteMessageRequest(sqsListenQueueUrl, messageRecieptHandle));
                            LOG.debug(String.format("Message %s of %s is processed and deleted from queue '%s'", i + 1,
                                    messages.size(), sqsListenQueueUrl));
                        }

                        boolean recovered = healthy.compareAndSet(false, true);
                        if (recovered) {
                            LOG.info(String.format("Queue '%s' recovered from error condition", sqsListenQueueUrl));
                        }
                    } catch (AmazonClientException ace) {
                        handleQueueError(ace);
                    }
                }
                LOG.info(interruptedMsg);
            }
        };
        pollingThread.start();
    }

    private void logProcessingError(Message msg, Exception e) {
        LOG.error("An error occurred while processing the following message:" + "\n\tMessageId:     " + msg.getMessageId()
                + "\n\tReceiptHandle: " + msg.getReceiptHandle() + "\n\tMD5OfBody:     " + msg.getMD5OfBody()
                + "\n\tBody:          " + msg.getBody(), e);
    }

    private void handleQueueError(AmazonClientException ace) {
        boolean firstAttempt = healthy.compareAndSet(true, false);
        String errorMsg = "An error occurred while listening to '%s', waiting '%s' ms before retrying...";
        if (!firstAttempt) {
            errorMsg = "Retry failed while listening to '%s', waiting '%s' ms before retrying...";
        }
        LOG.error(String.format(errorMsg, sqsListenQueueUrl, SLEEP_ON_ERROR), ace);
        try {
            Thread.sleep(SLEEP_ON_ERROR);
        } catch (InterruptedException ie) {
            LOG.info(interruptedMsg);
            return;
        }
    }

    @Override
    public void stop() throws Exception {
        pollingThread.interrupt();
    }

    @Override
    public boolean isHealthy() {
        return healthy.get();
    }

    @Override
    public String getQueueUrl() {
        return sqsListenQueueUrl;
    }

}
