package io.interact.sqsdw.core;

import com.amazonaws.AmazonWebServiceClient;
import io.dropwizard.lifecycle.Managed;
import io.interact.sqsdw.AwsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages an aws client with the Dropwizard lifecycle.
 */
public class ManagedAwsClient implements Managed {

    private static final Logger LOG = LoggerFactory.getLogger(AwsFactory.class);

    private AmazonWebServiceClient awsClient;

    private ManagedAwsClient() {
        // prevent instantiation
    }

    public ManagedAwsClient(AmazonWebServiceClient awsClient) {
        if (awsClient == null) {
            throw new IllegalArgumentException("Aws client cannot be null");
        }
        this.awsClient = awsClient;
    }

    @Override
    public void start() throws Exception {
        // Do nothing...
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Shutting down aws client, " + awsClient.getClass());
        awsClient.shutdown();
    }
    
}
