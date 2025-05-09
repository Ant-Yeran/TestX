package com.it.testx.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用删除请求类
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}

