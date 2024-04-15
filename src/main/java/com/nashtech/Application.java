package com.nashtech;

import com.nashtech.options.GCPOptions;
import com.nashtech.options.PipelineFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Application {
    public static void main(String[] args) {
        Pipeline pipeline = PipelineFactory.createPipeline(args);
        GCPOptions options = pipeline.getOptions().as(GCPOptions.class);

        //REST API endpoint
        String apiUrl = "https://my.api.mockaroo.com/3_fixed_band.json?key=dcbc8750";

        PCollection<String> dummyInput = pipeline.apply("CreateDummyInput", Create.of("dummy"));

        // Fetch data from the REST API endpoint
        PCollection<String> data = dummyInput.apply("FetchData", ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    c.output(response.toString());
                } else {
                    throw new RuntimeException("Failed to fetch data from API, status code: " + responseCode);
                }
            }
        }));

        // Send Data to GCS Bucket
        data.apply("Write to GCS bucket", TextIO.write().to(options.getGcsBucketName()));

        // Write Data to Pub/Sub Topic
        data.apply("Write to Pub/Sub Topic", PubsubIO.writeStrings().to(options.getTopic()));

        // Write data to a file
        data.apply("WriteDataToFile", TextIO.write().to("output.txt"));

        // Run the pipeline
        pipeline.run();
    }
}
