package com.it.testx.common;

import com.it.testx.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应信息
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}


