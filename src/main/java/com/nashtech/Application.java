package com.nashtech;

import com.nashtech.options.GCPOptions;
import com.nashtech.options.PipelineFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Application {
    final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Pipeline pipeline = PipelineFactory.createPipeline(args);
        GCPOptions options = pipeline.getOptions().as(GCPOptions.class);

        PCollection<@UnknownKeyFor @NonNull @Initialized PubsubMessage> messages;

        if (options.getUseSubscription()) {
            logger.info("Reading From Subscription");
            messages = pipeline.apply("Reading From Subscription", PubsubIO.readMessagesWithAttributes().fromSubscription(options.getInputSubscription()));
        } else {
            logger.info("Reading From Pub Sub Topic");
            messages = pipeline
                    // 1) Read string messages from a Pub/Sub topic.
                    .apply("Read PubSub Messages From Topic", PubsubIO.readMessagesWithAttributes().fromTopic(options.getInputTopic()));
        }

        PCollection<String> stringMessages = messages.apply("Convert to String", ParDo.of(new DoFn<PubsubMessage, String>() {
            @ProcessElement
            public void process(ProcessContext processContext) {
                PubsubMessage pubsubMessage = processContext.element();
                Optional<String> messageString = Optional.ofNullable(pubsubMessage)
                        .map(message -> new String(message.getPayload(), StandardCharsets.UTF_8));
                messageString.ifPresent(processContext::output);
            }
        }));

        if (options.getUseSubscription()) {
            logger.info("Writing messages to Pub/Sub Subscription");
            stringMessages.apply("Write to Pub/Sub subscription", PubsubIO.writeStrings().to(options.getOutputSubscription()));
        } else {
            logger.info("Writing messages to Pub/Sub Topic");
            stringMessages.apply("Write to Pub/Sub topic", PubsubIO.writeStrings().to(options.getOutputTopic()));
        }

        // Run the pipeline
        pipeline.run();
    }
}
