package com.it.testx.config.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Google Cloud Storage 客户端配置类
 */
@Configuration
@ConfigurationProperties(prefix = "gcp.storage")
@Data
public class GCSConfig {
    /**
     * Google Cloud 基本配置
     */
    private final CloudConfig cloudConfig;

    /**
     * 存储桶名称
     */
    private String bucket;

    /**
     * Google Cloud Storage 客户端
     */
    private Storage gcsClient;


    public GCSConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }

    @Bean
    @Primary
    public Storage gcsClient() throws IOException {
        if (gcsClient != null) {
            return gcsClient;
        }

        GoogleCredentials credentials;
        try (InputStream credentialsStream = new FileInputStream(cloudConfig.getCredentialsPath())) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
        }

        this.gcsClient = StorageOptions.newBuilder()
                .setProjectId(cloudConfig.getProjectId())
                .setCredentials(credentials)
                .build()
                .getService();

        return gcsClient;
    }

    @PreDestroy
    public void close() {
        if (gcsClient != null) {
            try {
                gcsClient.close();
            } catch (Exception e) {
                // TODO 记录日志但不要抛出异常
//                LoggerFactory.getLogger(GcsConfig.class)
//                        .warn("Error closing GCS storage client", e);
            }
        }
    }
}

//@Configuration
//@ConfigurationProperties(prefix = "gcp.storage")
//@Data
//public class GcsConfig {
//    @Resource
//    private final CloudConfig cloudConfig;
//
//    /**
//     * 存储桶名称
//     */
//    private String bucket;
//
////    @Value("${gcp.project-id}")
////    private String projectId;
////
////    @Value("${gcp.credentials.path}")
////    private String credentialsPath;
////
////    @Value("${gcp.storage.bucket-name}")
////    private String bucketName;
//
//    @Bean
//    public Storage storage() throws IOException {
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(cloudConfig.getCredentialsPath()));
//        return StorageOptions.newBuilder()
//                .setProjectId(cloudConfig.getProjectId())
//                .setCredentials(credentials)
//                .build()
//                .getService();
//    }
//
//    /**
//     * 配置 GCS 下载器 Bean
//     */
//    @Bean
//    public GcsDownloader gcsDownloader(Storage storage) {
//        return new GcsDownloader(storage, bucket);
//    }
//
//    /**
//     * GCS 文件下载器封装类
//     */
//    public static class GcsDownloader {
//        private final Storage storage;
//        private final String defaultBucket;
//
//        public GcsDownloader(Storage storage, String defaultBucket) {
//            this.storage = storage;
//            this.defaultBucket = defaultBucket;
//        }
//
//        /**
//         * 下载文件到指定路径
//         * @param objectName GCS对象名称
//         * @param destFilePath 本地目标路径
//         */
//        public void downloadFile(String objectName, String destFilePath) {
//            downloadFile(defaultBucket, objectName, destFilePath);
//        }
//
//        /**
//         * 下载文件到指定路径（指定bucket）
//         * @param bucketName GCS bucket名称
//         * @param objectName GCS对象名称
//         * @param destFilePath 本地目标路径
//         */
//        public void downloadFile(String bucketName, String objectName, String destFilePath) {
//            try {
//                Blob blob = storage.get(BlobId.of(bucketName, objectName));
//                if (blob == null) {
//                    throw new RuntimeException("Object not found in bucket");
//                }
//                blob.downloadTo(Paths.get(destFilePath));
//                System.out.printf("Downloaded object %s from bucket %s to %s%n",
//                    objectName, bucketName, destFilePath);
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to download file from GCS", e);
//            }
//        }
//    }
//
//    /**
//     * 配置 GCS 上传器 Bean
//     */
//    @Bean
//    public GcsUploader gcsUploader(Storage storage) {
//        return new GcsUploader(storage, bucket);
//    }
//
//    /**
//     * GCS 文件上传器封装类
//     */
//    public static class GcsUploader {
//        private final Storage storage;
//        private final String defaultBucket;
//
//        public GcsUploader(Storage storage, String defaultBucket) {
//            this.storage = storage;
//            this.defaultBucket = defaultBucket;
//        }
//
//        /**
//         * GCS 文件上传器方法
//         * @param bucketName 存储桶名称
//         * @param objectName 对象名称(可包含路径如 "folder/subfolder/filename.txt")
//         * @param sourceFilePath 本地源文件路径
//         */
//        public void uploadFile(String bucketName, String objectName, String sourceFilePath) {
//            try {
//                BlobId blobId = BlobId.of(bucketName, objectName);
//                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//                storage.create(blobInfo, Files.readAllBytes(Paths.get(sourceFilePath)));
//                System.out.printf("Uploaded file %s to gs://%s/%s%n",
//                        sourceFilePath, bucketName, objectName);
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to upload file to GCS", e);
//            }
//        }
//
//        /**
//         * 使用默认存储桶上传文件
//         * @param objectName 对象名称(可包含路径)
//         * @param sourceFilePath 本地源文件路径
//         */
//        public void uploadFile(String objectName, String sourceFilePath) {
//            uploadFile(defaultBucket, objectName, sourceFilePath);
//        }
//    }
//}