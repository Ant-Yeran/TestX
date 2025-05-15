# Create Database
CREATE DATABASE IF NOT EXISTS testx
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE testx;

# User
CREATE TABLE IF NOT EXISTS user
(
    id            BIGINT AUTO_INCREMENT COMMENT 'Primary key ID' PRIMARY KEY,
    user_account  VARCHAR(256)                           NOT NULL COMMENT 'User account',
    user_password VARCHAR(512)                           NOT NULL COMMENT 'User password (encrypted)',
    user_name     VARCHAR(256)                           NULL COMMENT 'User nickname',
    user_avatar   VARCHAR(1024)                          NULL COMMENT 'User avatar URL',
    user_profile  VARCHAR(512)                           NULL COMMENT 'User profile/bio',
    user_role     VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT 'User role: user/admin',
    edit_time     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Last edit time',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Creation time',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
    is_delete     TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Soft delete flag (0=active, 1=deleted)',

    -- Constraints
    -- 核心约束：仅对 is_delete=0 的账号强制唯一
    UNIQUE KEY uk_user_account_active (user_account, (IF(is_delete = 0, 1, NULL))),
    INDEX idx_user_name (user_name)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT ='User information table';
