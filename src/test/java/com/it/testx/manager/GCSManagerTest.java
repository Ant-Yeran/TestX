package com.it.testx.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class GCSManagerTest {

    @Resource
    private GCSManager gcsManager;

    private final String folder = "Test";

    private final String objectName = "TestObject.java";

    private final String rootPath = "/Users/ye/Desktop/Code/TestX/src/test/java/com/it/testx/manager";

    @Test
    public void uploadObject() throws IOException {
        // The path to your file to update
        String sourceFilePath = rootPath + "/" + objectName;

        gcsManager.uploadFile(folder + "/" + objectName, sourceFilePath);
    }

    @Test
    public void downloadObject() throws IOException {
        // The path to your file to download
        String destFilePath = rootPath + "/" + objectName;

        gcsManager.downloadFile(folder + "/" + objectName, destFilePath);
    }
}