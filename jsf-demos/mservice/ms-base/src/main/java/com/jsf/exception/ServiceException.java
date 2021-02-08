package com.jsf.exception;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-08
 * Time: 13:11
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
        super("ERROR");
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
