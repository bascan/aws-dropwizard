package io.interact.sqsdw.sqs;

import io.dropwizard.lifecycle.Managed;

import com.amazonaws.services.sqs.AmazonSQS;

/**
 * Managed {@link AmazonSQS} queue listener.
 * 
 * @author Bas Cancrinus
 */
public interface SqsListener extends Managed {

    /**
     * Health check of the associated SQS queue.
     * 
     * @return True when the SQS queue for this instance is healthy, false
     *         otherwise.
     */
    boolean isHealthy();

    /**
     * @return The URL of the associated SQS queue.
     */
    String getQueueUrl();
}
