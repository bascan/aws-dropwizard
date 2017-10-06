# aws-dropwizard
You can find the latest release on Maven Central: <http://search.maven.org> under:
- Group ID: ``io.interact``
- Artifact ID: ``sqs-dropwizard``

## Introduction

sqs-sns-dropwizard is a utility library that integrates the Amazon SQS and SNS offerings with the Dropwizard REST framework.
It contains convenience classes for sending messages to - and receiving from - SQS queues while being managed
by the Dropwizard framework. It also supports creating and managing SNS clients for push notifications.

## Getting started
- Add the following settings to your configuration yaml file:

````yaml
# Amazon SQS settings.
awsFactory:
  awsAccessKeyId: ...
  awsSecretKey: ...
  awsRegion: ...

sqsListenQueueUrl: https://sqs...
````

- Add the Aws factory and the listen queue URL to your configuration class:

````java
    @Valid
    @NotNull
    @JsonProperty
    private AwsFactory awsFactory;

    @NotNull
    @JsonProperty
    private String sqsListenQueueUrl;

    public AwsFactory getAwsFactory() {
        return sqsFactory;
    }

    public void setAwsFactory(AwsFactory awsFactory) {
        this.sqsFactory = sqsFactory;
    }

    public String getSqsListenQueueUrl() {
        return sqsListenQueueUrl;
    }

    public void setSqsListenQueueUrl(String sqsListenQueueUrl) {
        this.sqsListenQueueUrl = sqsListenQueueUrl;
    }
````

## SQS

- Extend the MessageHandler class and process the messages that you expect to receive in the handle() method
(you can register multiple MessageHandler instances with the queue listener):

````java
package ...;

import io.interact.sqsdw.sqs.MessageHandler;

public class MessageHandlerImpl extends MessageHandler {

	public MessageHandlerImpl() {
        super("MyMessageType");
    }

    public void handle(Message message) {
		// Message processing here.
    }

}
````

- Register the queue listener in the run() method of your application class
(you can inject the constructor arguments into an SqsListenerImpl instance with Guice):

````java
    @Override
    public void run(IlinkSfdcConfiguration conf, Environment env) {
        final AmazonSQS sqs = conf.getSqsFactory().buildSQSClient(env);

        final MessageHandler handler = ...

        final Set<MessageHandler> handlers = new HashSet<>();
        handlers.add(handler);
        
        final SqsListener sqsListener = new SqsListenerImpl(sqs, conf.getSqsListenQueueUrl(), handlers);

        env.lifecycle().manage(sqsListener);
        env.healthChecks().register("SqsListener", new SqsListenerHealthCheck(sqsListener));
    }
````

- Send messages to SQS from your client with the MessageDispatcher helper class:

````java
MessageDispatcher.dispatch(yourData, queueUrl, "MyMessageType", sqs);
````

Dispatched messages of type "MyMessageType" will be handled by your MessageHandlerImpl class now.
You can loosely couple clients and message handlers by using several message types in your application(s).

You'll now have an extra health check called "SqsListener" that monitors the health of your queue.


## SNS

- You can also build an SNS client with your AwsFactory instance using the credentials and region specified in your .yaml file. The client will automatically be shutdown at the end of the application's lifecycle.

````java
    @Override
    public void run(IlinkSfdcConfiguration conf, Environment env) {
        final AmazonSNS sns = conf.getSqsFactory().buildSNSClient(env);
        sns.publish("arn", "hello world");
   
    }
````


That's it!
