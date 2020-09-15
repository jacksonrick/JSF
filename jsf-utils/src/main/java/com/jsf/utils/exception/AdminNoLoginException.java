package com.jsf.utils.exception;

import com.jsf.utils.annotation.Except;

/**
 * Admin未登录
 * Created by xujunfei on 2018/5/24.
 */
@Except(error = false, stack = false)
public class AdminNoLoginException extends RuntimeException {

    public AdminNoLoginException(String message) {
        super(message);
    }

}
