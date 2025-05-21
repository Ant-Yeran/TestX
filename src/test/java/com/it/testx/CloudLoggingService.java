package com.it.testx;

import com.google.api.gax.paging.Page;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.*;
import java.util.List;
import java.util.Collections;

/**
 * Google Cloud Logging 服务封装
 */
public interface CloudLoggingService {

    /**
     * 初始化日志客户端
     */
    void initialize();

    /**
     * 写入日志
     * @param logName 日志名称
     * @param severity 日志级别
     * @param message 日志消息
     */
    void writeLog(String logName, Severity severity, String message);

    /**
     * 读取日志
     * @param logName 日志名称
     * @param filter 过滤条件
     * @param maxResults 最大返回数量
     * @return 日志条目列表
     */
    List<LogEntry> readLogs(String logName, String filter, int maxResults);

    /**
     * 关闭客户端
     */
    void shutdown();
}

/**
 * Google Cloud Logging 服务实现
 */
class GoogleCloudLoggingServiceImpl implements CloudLoggingService {
    private Logging logging;

    @Override
    public void initialize() {
        this.logging = LoggingOptions.getDefaultInstance().getService();
    }

    @Override
    public void writeLog(String logName, Severity severity, String message) {
        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(message))
                .setSeverity(severity)
                .setLogName(logName)
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        logging.write(Collections.singleton(entry));
        logging.flush();
    }

    @Override
    public List<LogEntry> readLogs(String logName, String filter, int maxResults) {
        String fullFilter = String.format("logName=\"projects/%s/logs/%s\" %s",
                LoggingOptions.getDefaultInstance().getProjectId(), logName, filter);

        Page<LogEntry> entries = logging.listLogEntries(
                Logging.EntryListOption.filter(fullFilter),
                Logging.EntryListOption.pageSize(maxResults));

        return (List<LogEntry>) entries.getValues();
    }

    @Override
    public void shutdown() {
        if (logging != null) {
            try {
                logging.close();
            } catch (Exception e) {
                System.err.println("Error closing logging client: " + e.getMessage());
            }
        }
    }
}

/**
 * 使用示例
 */
class LoggingExample {
    public static void main(String[] args) {
        CloudLoggingService loggingService = new GoogleCloudLoggingServiceImpl();
        
        try {
            // 初始化
            loggingService.initialize();
            
            // 写入日志
            loggingService.writeLog("my-log", Severity.ERROR, "Hello, world!");
            
            // 读取日志
            List<LogEntry> logs = loggingService.readLogs("my-log", "severity=ERROR", 10);
            System.out.println("Retrieved logs:");
            for (LogEntry log : logs) {
                System.out.println(log.getPayload().getData());
            }
        } finally {
            // 关闭
            loggingService.shutdown();
        }
    }
}