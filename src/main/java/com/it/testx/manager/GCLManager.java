package com.it.testx.manager;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.it.testx.exception.BusinessException;
import com.it.testx.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Google Cloud Logging 通用类
 */
@Component
public class GCLManager {
    @Resource
    private Logging gclClient;

    /**
     * 写入日志
     *
     * @param logName 日志名称，用于分类和筛选日志
     * @param severity 日志级别，如 DEBUG、INFO、WARNING、ERROR 等
     * @param message 日志消息内容
     */
    public void writeLog(String logName, Severity severity, String message) throws BusinessException {
        try {
            LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(message))
                    .setSeverity(severity)
                    .setLogName(logName)
                    .setResource(MonitoredResource.newBuilder("global").build())
                    .build();
            System.out.println(Collections.singleton(entry));
            gclClient.write(Collections.singleton(entry));
            gclClient.flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "日志写入失败，" + e.getMessage());
        }
    }
}
