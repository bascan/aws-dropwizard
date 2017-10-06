package io.interact.sqsdw;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import io.interact.sqsdw.core.ManagedAwsClient;
import org.junit.Test;

import static org.junit.Assert.*;

public class ManagedAwsClientTest {

    @Test
    public void testRefuseNullParameters() throws Exception {
       try {
           new ManagedAwsClient(null);
       } catch (IllegalArgumentException e) {
           assertNotNull(e);
       }
    }


    @Test
    public void testCreateWithSQSClient() throws Exception {
        try {
            AmazonSQSClient sqsClient = new AmazonSQSClient();
            new ManagedAwsClient(sqsClient);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateWithSNSClient() throws Exception {
        try {
            AmazonSNSClient snsClient = new AmazonSNSClient();
            new ManagedAwsClient(snsClient);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

}
