package com.it.testx;

import com.google.api.Metric;
import com.google.api.MonitoredResource;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.*;
import com.google.protobuf.util.Timestamps;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class MonitoringService {

    // 配置常量
    private static final String PROJECT_ID = "your-project-id";
    private static final String METRIC_TYPE = "custom.googleapis.com/stores/daily_sales";
    private static final String RESOURCE_TYPE = "global"; // 可根据实际资源类型调整
    
    @Test
    public void main() {
        try {
            submitCustomMetric(
                PROJECT_ID,
                METRIC_TYPE,
                Collections.singletonMap("store_id", "Pittsburg"),
                123.45,
                Collections.singletonMap("project_id", PROJECT_ID)
            );
        } catch (Exception e) {
            System.err.println("Error submitting metric: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 提交自定义指标到Cloud Monitoring
     * 
     * @param projectId GCP项目ID
     * @param metricType 指标类型(如custom.googleapis.com/your_metric)
     * @param metricLabels 指标标签
     * @param value 指标值
     * @param resourceLabels 资源标签
     */
    public void submitCustomMetric(
            String projectId,
            String metricType,
            Map<String, String> metricLabels,
            double value,
            Map<String, String> resourceLabels) throws IOException {

        // 验证输入参数
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project ID must be specified");
        }

        // 使用try-with-resources确保客户端正确关闭
        try (MetricServiceClient metricServiceClient = MetricServiceClient.create()) {
            
            // 1. 准备数据点
            TimeInterval interval = TimeInterval.newBuilder()
                .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();
            
            Point point = Point.newBuilder()
                .setInterval(interval)
                .setValue(TypedValue.newBuilder().setDoubleValue(value).build()).build();

            // 2. 准备指标描述符
            Metric.Builder metricBuilder = Metric.newBuilder()
                .setType(metricType);
            
            if (metricLabels != null) {
                metricBuilder.putAllLabels(metricLabels);
            }

            // 3. 准备监控资源描述符
            MonitoredResource.Builder resourceBuilder = MonitoredResource.newBuilder()
                .setType(RESOURCE_TYPE);
            
            if (resourceLabels != null) {
                resourceBuilder.putAllLabels(resourceLabels);
            }

            // 4. 构建时间序列请求
            TimeSeries timeSeries = TimeSeries.newBuilder()
                .setMetric(metricBuilder.build())
                .setResource(resourceBuilder.build())
                .addPoints(point)
                .build();

            // 5. 创建并发送请求
            CreateTimeSeriesRequest request = CreateTimeSeriesRequest.newBuilder()
                .setName(ProjectName.of(projectId).toString())
                .addTimeSeries(timeSeries)
                .build();

            // 6. 执行写入操作(添加重试逻辑)
            try {
                metricServiceClient.createTimeSeries(request);
                System.out.printf("Successfully submitted metric %s with value %.2f%n", 
                    metricType, value);
            } catch (Exception e) {
                System.err.println("Failed to submit metric: " + e.getMessage());
                throw e;
            }
        }
    }
}