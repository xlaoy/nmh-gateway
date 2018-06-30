package com.xlaoy.nmhgateway.exception;

/**
 * Created by Administrator on 2018/6/30 0030.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
