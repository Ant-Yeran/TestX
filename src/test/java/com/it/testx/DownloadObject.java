package com.it.testx;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.it.testx.manager.GCSManager;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class DownloadObject {

  @Resource
  private GCSManager gcsManager;


  @Test
  public void downloadObject() throws IOException {
//    // The ID of your GCP project
//    String projectId = "testx-459207";
//
//    // The ID to give your GCS bucket
//    String bucketName = "testx_bucket";
//
////    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//    String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
//    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
//    Storage storage = StorageOptions.newBuilder()
//            .setProjectId(projectId)
//            .setCredentials(credentials)
//            .build()
//            .getService();

    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to which the file should be downloaded
    // String destFilePath = "/local/path/to/file.txt";

    // The ID of your GCS object
    String objectName = "UploadObject.java";

    // The path to your file to download
    String destFilePath = "/Users/ye/Desktop/Code/TestX/src/test/java/com/it/testx/Down.java";

    gcsManager.downloadFile(objectName, destFilePath);

//    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

//    Blob blob = storage.get(BlobId.of(bucketName, objectName));
//    blob.downloadTo(Paths.get(destFilePath));
//
//    System.out.println(
//        "Downloaded object "
//            + objectName
//            + " from bucket name "
//            + bucketName
//            + " to "
//            + destFilePath);
  }
}