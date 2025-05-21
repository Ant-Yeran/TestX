package com.it.testx.manager;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.it.testx.config.gcp.GCSConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Google Cloud Storage 通用类
 */
@Component
public class GCSManager {

    @Resource
    private GCSConfig gcsConfig;

    @Resource
    private Storage gcsClient;

    /**
     * 文件上传
     *
     * @param bucketName     存储桶名称
     * @param objectName     对象名称(可包含路径如 "folder/subfolder/filename.txt")
     * @param sourceFilePath 本地源文件路径
     */
    public void uploadFile(String bucketName, String objectName, String sourceFilePath) {
        try {
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            gcsClient.create(blobInfo, Files.readAllBytes(Paths.get(sourceFilePath)));
            System.out.printf("Uploaded file %s to gs://%s/%s%n",
                    sourceFilePath, bucketName, objectName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to GCS", e);
        }
    }

    /**
     * 文件上传（使用默认存储桶）
     *
     * @param objectName     对象名称(可包含路径)
     * @param sourceFilePath 本地源文件路径
     */
    public void uploadFile(String objectName, String sourceFilePath) {
        uploadFile(gcsConfig.getBucket(), objectName, sourceFilePath);
    }

    /**
     * 文件下载
     *
     * @param bucketName   存储桶
     * @param objectName   对象名称(可包含路径)
     * @param destFilePath 本地目标路径
     */
    public void downloadFile(String bucketName, String objectName, String destFilePath) {
        try {
            Blob blob = gcsClient.get(BlobId.of(bucketName, objectName));
            if (blob == null) {
                throw new RuntimeException("Object not found in bucket");
            }
            blob.downloadTo(Paths.get(destFilePath));
            System.out.printf("Downloaded object %s from bucket %s to %s%n",
                    objectName, bucketName, destFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from GCS", e);
        }
    }

    /**ß
     * 文件下载
     *
     * @param objectName   对象名称(可包含路径)
     * @param destFilePath 本地目标路径
     */
    public void downloadFile(String objectName, String destFilePath) {
        downloadFile(gcsConfig.getBucket(), objectName, destFilePath);
    }
}
