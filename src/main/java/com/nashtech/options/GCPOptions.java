package com.nashtech.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.storage.Storage;
import com.nashtech.service.GcsClientFactory;
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

    @Description("GCS Bucket Name")
    @Default.String("gs://code_combat_resumes/resumes-data")
    String getGcsBucketName();

    void setGcsBucketName(String gcsBucketName);

    @Description("GCS File Name")
    @Default.String("resumes-data/person-resume-data")
    String getGcsFileName();

    void setGcsFileName(String gcsFileName);

    @JsonIgnore
    @Description("GCS Client")
    @Default.InstanceFactory(GcsClientFactory.class)
    Storage getGcsClient();

    void setGcsClient(Storage gcsClient);

    @Description("The Cloud Pub/Sub topic to read from.")
    @Validation.Required
    @Default.String("projects/nashtech-ai-dev-389315/topics/resume-storage-location")
    String getTopic();

    void setTopic(String value);
}
