package com.jsf.utils.exception;

import com.jsf.utils.annotation.Except;

/**
 * api异常
 * Created by xujunfei on 2018/5/24.
 */
@Except(error = true, stack = false)
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
