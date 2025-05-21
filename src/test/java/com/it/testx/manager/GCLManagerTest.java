package com.it.testx.manager;

import com.google.cloud.logging.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import com.google.api.gax.paging.Page;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;

@SpringBootTest
class GCLManagerTest {

    @Resource
    private GCLManager gclManager;

    @Test
    // 跑代码时需要关闭代理
    void writeLog() {
        gclManager.writeLog("my-log", Severity.INFO, "This is a test log.");
    }


    @Test
    public void listLogs() throws Exception {

        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {

            // List all log names
            Page<String> logNames = logging.listLogs();
            while (logNames != null) {
                for (String logName : logNames.iterateAll()) {
                    System.out.println(logName);
                }
                logNames = logNames.getNextPage();
            }
        }
    }
}