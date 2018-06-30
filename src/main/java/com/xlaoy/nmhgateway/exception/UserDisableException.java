package com.xlaoy.nmhgateway.exception;

/**
 * Created by Administrator on 2018/6/30 0030.
 */
public class UserDisableException extends RuntimeException {

    public UserDisableException() {
    }

    public UserDisableException(String message) {
        super(message);
    }
}
