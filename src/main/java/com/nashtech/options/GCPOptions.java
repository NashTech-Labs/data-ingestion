package com.nashtech.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.storage.Storage;
import com.nashtech.services.GcsClientFactory;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;

public interface GCPOptions extends PipelineOptions {
    @Description("GCP project to access")
    @Validation.Required
    @Default.String("nashtech-ai-dev-389315")
    String getGcpProject();

    void setGcpProject(String gcpProject);

    @Description("The Cloud Pub/Sub topic to read from")
    @Validation.Required
    @Default.String("projects/nashtech-ai-dev-389315/topics/resume-input-topic")
    String getInputTopic();

    void setInputTopic(String inputTopic);

    @Description("The cloud Pub/Sub subscription to read from")
    @Validation.Required
    @Default.String("projects/nashtech-ai-dev-389315/subscriptions/resume-input-topic-sub")
    String getInputSubscription();

    void setInputSubscription(String inputSubscription);

    @Description("The Cloud Pub/Sub topic to write")
    @Validation.Required
    @Default.String("projects/nashtech-ai-dev-389315/topics/resume-output-topic")
    String getOutputTopic();

    void setOutputTopic(String outputTopic);

    @Description("The cloud Pub/Sub subscription to write")
    @Validation.Required
    @Default.String("projects/nashtech-ai-dev-389315/subscriptions/resume-output-topic-sub")
    String getOutputSubscription();

    void setOutputSubscription(String outputSubscription);

    @Description("Whether to use topic or subscription")
    @Validation.Required
    @Default.Boolean(true)
    Boolean getUseSubscription();

    void setUseSubscription(Boolean useSubscription);

    @JsonIgnore
    @Description("GCS Client")
    @Default.InstanceFactory(GcsClientFactory.class)
    Storage getGcsClient();

    void setGcsClient(Storage gcsClient);

    @Description("GCS Bucket Name")
    @Default.String("resume-data-kode-kombat")
    String getGcsBucketName();

    void setGcsBucketName(String gcsBucketName);

    @Description("GCS File Name")
    @Default.String("resume-data/resumesData")
    String getGcsFileName();

    void setGcsFileName(String gcsFileName);
}
