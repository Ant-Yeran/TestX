package com.it.testx.manager;

import com.google.cloud.kms.v1.*;
import com.google.protobuf.ByteString;
import com.it.testx.config.gcp.CloudConfig;
import com.it.testx.exception.BusinessException;
import com.it.testx.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Base64;

/**
 * Google Cloud KMS 通用类
 */
@Component
public class KmsManager {
    @Resource
    private KeyManagementServiceClient kmsClient;

    @Resource
    private CloudConfig cloudConfig;

    /**
     * 创建密钥环
     *
     * @param locationId 位置ID，如 "global", "us-east1" 等
     * @param keyRingId 密钥环ID
     * @return 创建的密钥环
     * @throws BusinessException 如果操作失败
     */
    public KeyRing createKeyRing(String locationId, String keyRingId) throws BusinessException {
        try {
            LocationName parent = LocationName.of(cloudConfig.getProjectId(), locationId);
            return kmsClient.createKeyRing(parent, keyRingId, KeyRing.newBuilder().build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建密钥环失败: " + e.getMessage());
        }
    }

    /**
     * 创建加密密钥
     *
     * @param locationId 位置ID
     * @param keyRingId 密钥环ID
     * @param keyId 密钥ID
     * @return 创建的加密密钥
     * @throws BusinessException 如果操作失败
     */
    public CryptoKey createCryptoKey(String locationId, String keyRingId, String keyId) throws BusinessException {
        try {
            KeyRingName parent = KeyRingName.of(cloudConfig.getProjectId(), locationId, keyRingId);

            CryptoKey cryptoKey = CryptoKey.newBuilder()
                    .setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT)
                    .setVersionTemplate(CryptoKeyVersionTemplate.newBuilder()
                            .setAlgorithm(CryptoKeyVersion.CryptoKeyVersionAlgorithm.GOOGLE_SYMMETRIC_ENCRYPTION)
                            .build())
                    .build();

            return kmsClient.createCryptoKey(parent, keyId, cryptoKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建加密密钥失败: " + e.getMessage());
        }
    }

    /**
     * 加密文本
     *
     * @param locationId 位置ID
     * @param keyRingId 密钥环ID
     * @param keyId 密钥ID
     * @param plaintext 要加密的明文
     * @return 加密后的密文(Base64编码)
     * @throws BusinessException 如果操作失败
     */
    public String encrypt(String locationId, String keyRingId, String keyId, String plaintext) throws BusinessException {
        try {
            CryptoKeyName keyName = CryptoKeyName.of(cloudConfig.getProjectId(), locationId, keyRingId, keyId);
            ByteString plaintextBytes = ByteString.copyFromUtf8(plaintext);
            EncryptResponse response = kmsClient.encrypt(keyName, plaintextBytes);
            return Base64.getEncoder().encodeToString(response.getCiphertext().toByteArray());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "加密失败: " + e.getMessage());
        }
    }

    /**
     * 解密文本
     *
     * @param locationId 位置ID
     * @param keyRingId 密钥环ID
     * @param keyId 密钥ID
     * @param ciphertext 要解密的密文(Base64编码)
     * @return 解密后的明文
     * @throws BusinessException 如果操作失败
     */
    public String decrypt(String locationId, String keyRingId, String keyId, String ciphertext) throws BusinessException {
        try {
            CryptoKeyName keyName = CryptoKeyName.of(cloudConfig.getProjectId(), locationId, keyRingId, keyId);
            byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
            DecryptResponse response = kmsClient.decrypt(keyName, ByteString.copyFrom(ciphertextBytes));
            return response.getPlaintext().toStringUtf8();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "解密失败: " + e.getMessage());
        }
    }
}