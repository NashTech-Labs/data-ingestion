package com.nashtech.transformations;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.nashtech.options.GCPOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.nashtech.utils.PipelineConstants.errorTag;
import static com.nashtech.utils.PipelineConstants.successTag;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SendDataToGCSBucket extends DoFn<String, String> {
    final Logger logger = LoggerFactory.getLogger(SendDataToGCSBucket.class);

    @ProcessElement
    public void processElement(ProcessContext context, PipelineOptions options) {
        String element = context.element();
        GCPOptions gcpOptions = options.as(GCPOptions.class);
        Storage storage = gcpOptions.getGcsClient();

        try {
            BlobId blobId = BlobId.of(gcpOptions.getGcsBucketName(), gcpOptions.getGcsFileName());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            Blob blob = storage.create(blobInfo, Objects.requireNonNull(element).getBytes(UTF_8));

            String blobUri = String.format("gs://%s/%s", blob.getBucket(), blob.getName());
            context.output(successTag, blobUri);
        } catch (Exception ex) {
            logger.error("Error writing to GCS: " + ex.getMessage(), ex);
            String errorMessage = String.format("Error processing element %s: %s", element, ex.getMessage());
            context.output(errorTag, errorMessage);
        }
    }
}
