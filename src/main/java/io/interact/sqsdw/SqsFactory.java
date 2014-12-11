package io.interact.sqsdw;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Builds a managed {@link AmazonSQS} instance.
 * 
 * @author Bas Cancrinus
 */
public class SqsFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SqsFactory.class);

    @NotEmpty
    @JsonProperty
    private String awsAccessKeyId;

    @NotEmpty
    @JsonProperty
    private String awsSecretKey;

    @NotEmpty
    @JsonProperty
    private String awsRegion;

    /**
     * Builds an {@link AmazonSQS} instance that is managed by the server's
     * lifecycle.
     * 
     * @param env
     *            The environment where the {@link AmazonSQS} will be
     *            registered.
     * @return A managed instance.
     */
    public AmazonSQS build(Environment env) {
        LOG.info("Initialize Amazon SQS entry point");

        AWSCredentials credentials = new AWSCredentials() {

            @Override
            public String getAWSSecretKey() {
                return awsSecretKey;
            }

            @Override
            public String getAWSAccessKeyId() {
                return awsAccessKeyId;
            }
        };

        final AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region region = Region.getRegion(Regions.fromName(awsRegion));
        sqs.setRegion(region);

        env.lifecycle().manage(new Managed() {

            @Override
            public void start() {
                // NOOP
            }

            @Override
            public void stop() {
                LOG.info("Shutdown Amazon SQS entry point");
                sqs.shutdown();
            }
        });

        return sqs;
    }

    // Getters and setters.

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }
}
