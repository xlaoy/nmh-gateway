package com.xlaoy.nmhgateway.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by Administrator on 2018/6/30 0030.
 */
public class UserNotFoundException extends AccountStatusException {

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
