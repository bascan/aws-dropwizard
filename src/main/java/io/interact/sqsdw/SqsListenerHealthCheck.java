package io.interact.sqsdw;

import com.codahale.metrics.health.HealthCheck;

/**
 * Implements a health check for the {@link SqsListener}.
 * 
 * @author Bas Cancrinus
 */
public class SqsListenerHealthCheck extends HealthCheck {

    private SqsListener sqsListener;

    public SqsListenerHealthCheck(SqsListener sqsListener) {
        this.sqsListener = sqsListener;
    }

    @Override
    protected Result check() throws Exception {
        if (sqsListener.isHealthy()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("There is a problem with the SQS listener for queue: " + sqsListener.getQueueUrl());
        }
    }
}
