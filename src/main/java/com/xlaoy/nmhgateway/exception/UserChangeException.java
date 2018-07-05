package com.xlaoy.nmhgateway.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by Administrator on 2018/6/30 0030.
 */
public class UserChangeException extends AccountStatusException {

    public UserChangeException(String msg) {
        super(msg);
    }

    public UserChangeException(String msg, Throwable t) {
        super(msg, t);
    }
}
