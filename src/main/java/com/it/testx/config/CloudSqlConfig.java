package com.it.testx.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class CloudSqlConfig {

    @Bean
    @Primary  // 标记为主数据源，覆盖默认的MySQL数据源
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql:///%s", System.getenv("MYSQL_DB")));
        config.setUsername(System.getenv("MYSQL_USER")); // e.g. "root", "mysql"
        config.setPassword(System.getenv("MYSQL_PASS")); // e.g. "my-password"
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", System.getenv("MYSQL_CONNECTION_NAME"));

        return new HikariDataSource(config);
    }
}

//    @Value("${spring.cloud.gcp.sql.instance-connection-name}")
//    private String instanceConnectionName;
//
//    @Value("${spring.datasource.username}")
//    private String dbUser;
//
//    @Value("${spring.datasource.password}")
//    private String dbPass;
//
//    @Value("${spring.datasource.name}")
//    private String dbName;
//
//    @Bean
//    @Primary  // 标记为主数据源，覆盖默认的MySQL数据源
//    public DataSource dataSource() {
//        HikariConfig config = new HikariConfig();
//
//        // 配置基本连接信息
//        config.setJdbcUrl(String.format("jdbc:mysql:///%s", dbName));
//        config.setUsername(dbUser);
//        config.setPassword(dbPass);
//
//        // Cloud SQL 特定配置
//        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
//        config.addDataSourceProperty("cloudSqlInstance", instanceConnectionName);
//        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
//        config.addDataSourceProperty("cloudSqlRefreshStrategy", "lazy");
//
//        // 连接池优化配置
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//        config.setConnectionTimeout(30000);
//        config.setIdleTimeout(600000);
//        config.setMaxLifetime(1800000);
//        config.setPoolName("CloudSqlConnectionPool");
//
//        return new HikariDataSource(config);
//    }
//}