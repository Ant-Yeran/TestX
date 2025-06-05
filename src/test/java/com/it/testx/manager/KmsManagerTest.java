package com.it.testx.manager;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.KeyRing;
import com.it.testx.exception.BusinessException;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KmsManagerTest {

    @Resource
    private KmsManager kmsManager;

    public void demoKmsOperations() {
        try {
            // 创建密钥环
            KeyRing keyRing = kmsManager.createKeyRing("global", "my-key-ring");

            // 创建加密密钥
            CryptoKey cryptoKey = kmsManager.createCryptoKey("global", "my-key-ring", "my-key");

            // 加密数据
            String ciphertext = kmsManager.encrypt("global", "my-key-ring", "my-key", "敏感数据");

            // 解密数据
            String plaintext = kmsManager.decrypt("global", "my-key-ring", "my-key", ciphertext);

            System.out.println("解密结果: " + plaintext);

        } catch (BusinessException e) {
            // 处理异常
            e.printStackTrace();
        }
    }
}