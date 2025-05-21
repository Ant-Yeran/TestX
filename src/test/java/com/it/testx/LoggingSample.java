package com.it.testx;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.Severity;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class LoggingSample {

  // 配置常量
  private static final String PROJECT_ID = "your-gcp-project-id"; // 替换为你的GCP项目ID
  private static final String LOG_BUCKET = "your-log-bucket";    // 替换为你的日志存储桶名称

  /** Expects a new or existing Cloud log name as the first argument. */
  @Test
  public void test() throws Exception {
    // The name of the log to write to
    String logName = "my-log"; // "my-log";
    String textPayload = "Hello, world!";

    // 配置Logging选项，明确指定项目和存储桶
    LoggingOptions options = LoggingOptions.newBuilder()
        .setProjectId(PROJECT_ID)
        .build();

    // Instantiates a client with explicit configuration
    try (Logging logging = options.getService()) {

      LogEntry entry =
          LogEntry.newBuilder(StringPayload.of(textPayload))
              .setSeverity(Severity.ERROR)
              .setLogName(logName)
              .setResource(MonitoredResource.newBuilder("global")
                  .addLabel("project_id", PROJECT_ID)
                  .addLabel("bucket", LOG_BUCKET)
                  .build())
              .build();

      // Writes the log entry asynchronously with explicit destination
      logging.write(Collections.singleton(entry));

      // Flush before closing
      logging.flush();
    }
    System.out.printf("Logged to project %s, bucket %s: %s%n", 
        PROJECT_ID, LOG_BUCKET, textPayload);
  }
}