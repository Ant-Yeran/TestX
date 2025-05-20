package com.it.testx;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateBucketWithStorageClassAndLocation {
  @Test
  public void createBucketWithStorageClassAndLocation() throws IOException {
    // The ID of your GCP project
     String projectId = "testx-459207";

    // The ID to give your GCS bucket
     String bucketName = "testx_bucket";

//    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
      String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
      GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

      Storage storage = StorageOptions.newBuilder()
              .setProjectId(projectId)
              .setCredentials(credentials)
              .build()
              .getService();

    // See the StorageClass documentation for other valid storage classes:
    // https://googleapis.dev/java/google-cloud-clients/latest/com/google/cloud/storage/StorageClass.html
    StorageClass storageClass = StorageClass.COLDLINE;

    // See this documentation for other valid locations:
    // http://g.co/cloud/storage/docs/bucket-locations#location-mr
    String location = "ASIA";

    Bucket bucket =
        storage.create(
            BucketInfo.newBuilder(bucketName)
                .setStorageClass(storageClass)
                .setLocation(location)
                .build());

    System.out.println(
        "Created bucket "
            + bucket.getName()
            + " in "
            + bucket.getLocation()
            + " with storage class "
            + bucket.getStorageClass());
  }
}