package com.ff.exception;

import lombok.Getter;

/**
 * 业务异常
 */
//加上getter
@Getter
public class BaseException extends RuntimeException {


    private final Integer code;

    public BaseException(String message) {
        super(message);
        this.code = 0;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
