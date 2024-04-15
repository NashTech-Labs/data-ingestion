package com.nashtech.transformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.nashtech.options.GCPOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SendDataToPuSubTopic extends DoFn<String, Void> {
    final Logger logger = LoggerFactory.getLogger(SendDataToPuSubTopic.class);

    @ProcessElement
    public void process(ProcessContext processContext, PipelineOptions options) {
            Publisher publisher;
            try {
                String element = processContext.element();
                GCPOptions gcpOptions = options.as(GCPOptions.class);
                TopicName topicName = TopicName.of(gcpOptions.getGcpProject(), gcpOptions.getTopic());
                // Create a publisher instance with default settings bound to the topic
                publisher = Publisher.newBuilder(topicName).build();
                ByteString data = ByteString.copyFromUtf8(element);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                String messageId = messageIdFuture.get();
                System.out.println("Published message ID: " + messageId);
            } catch (IOException | ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
    }
}
