package io.interact.sqsdw;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.regions.Regions.DEFAULT_REGION;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Builds a managed {@link AmazonSQS} instance.
 * 
 * @author Bas Cancrinus
 */
public class SqsFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SqsFactory.class);

    @JsonProperty
    private String awsAccessKeyId;

    @JsonProperty
    private String awsSecretKey;

    @JsonProperty
    private String awsRegion;

    @JsonIgnore
    private AmazonSQS sqs;

    /**
     * Builds an {@link AmazonSQS} instance that is managed by the server's
     * lifecycle. Reference: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
     * 
     * @param env
     *            The environment where the {@link AmazonSQS} will be
     *            registered.
     * @return A managed instance.
     */
    public AmazonSQS build(Environment env) {
        LOG.info("Initialize Amazon SQS entry point");

        if (isEmpty(awsAccessKeyId) || isEmpty(awsSecretKey)) {
            sqs = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
        } else {
            sqs = new AmazonSQSClient(new BasicAWSCredentials(awsAccessKeyId, awsSecretKey));
        }

        final Regions regions = isNotEmpty(awsRegion) ? Regions.fromName(awsRegion) : DEFAULT_REGION;
        sqs.setRegion(Region.getRegion(regions));

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
