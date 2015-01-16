# sqs-dropwizard

## Introduction

sqs-dropwizard is a utility library that integrates the Amazon SQS offering with the Dropwizard REST framework.
It contains convenience classes for sending messages to - and receiving from - SQS queues while being managed
by the Dropwizard framework.

## Getting started
- Add the following settings to your configuration yaml file:

````yaml
# Amazon SQS settings.
sqsFactory:
  awsAccessKeyId: ...
  awsSecretKey: ...
  awsRegion: ...

sqsListenQueueUrl: https://sqs...
````

- Add the SQS factory and the listen queue URL to your configuration class:

````java
    @Valid
    @NotNull
    @JsonProperty
    private SqsFactory sqsFactory;

    @NotNull
    @JsonProperty
    private String sqsListenQueueUrl;

    public SqsFactory getSqsFactory() {
        return sqsFactory;
    }

    public void setSqsFactory(SqsFactory sqsFactory) {
        this.sqsFactory = sqsFactory;
    }

    public String getSqsListenQueueUrl() {
        return sqsListenQueueUrl;
    }

    public void setSqsListenQueueUrl(String sqsListenQueueUrl) {
        this.sqsListenQueueUrl = sqsListenQueueUrl;
    }
````

- Extend the MessageHandler class and process the messages that you expect to receive in the handle() method
(you can register multiple MessageHandler instances with the queue listener):

````java
package ...;

import io.interact.sqsdw.MessageHandler;

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
        final AmazonSQS sqs = conf.getSqsFactory().build(env);

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

That's it!

Dispatched messages of type "MyMessageType" will be handled by your MessageHandlerImpl class now.
You can loosely couple clients and message handlers by using several message types in your application(s).

You'll now have an extra health check called "SqsListener" that monitors the health of your queue.