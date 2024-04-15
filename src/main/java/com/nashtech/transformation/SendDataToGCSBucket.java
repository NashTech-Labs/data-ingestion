package com.nashtech.transformation;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.nashtech.options.GCPOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SendDataToGCSBucket extends DoFn<String, Void> {
    final Logger logger = LoggerFactory.getLogger(SendDataToGCSBucket.class);

    @ProcessElement
    public void process(ProcessContext processContext, PipelineOptions options) {
        try {
            String element = processContext.element();
            GCPOptions gcpOptions = options.as(GCPOptions.class);
            Storage storage = gcpOptions.getGcsClient();
            BlobId blobId = BlobId.of(gcpOptions.getGcsBucketName(), gcpOptions.getGcsFileName());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            Blob blob = storage.create(blobInfo, element.getBytes(UTF_8));
        } catch (Exception ex) {
            logger.error(String.format("Exception Occurred {%s}", ex));
        }
    }
}
