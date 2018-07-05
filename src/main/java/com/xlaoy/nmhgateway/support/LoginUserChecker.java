package com.xlaoy.nmhgateway.support;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/7/5 0005.
 */
@Component
public class LoginUserChecker implements UserDetailsChecker {

    /**
     * 在redis里面查找用户，查看：
     * 是否存在
     * 是否被禁用
     * 是否需要重新登陆
     * @param toCheck
     */
    @Override
    public void check(UserDetails toCheck) {
        LoginUser loginUser = (LoginUser)toCheck;
        //throw new UserNotFoundException("用户没查到");
        //throw new DisabledException("用户已禁用");
        //throw new LockedException("用户已锁定");
        //throw new UserChangeException("用户信息有变化，需要重新登陆");
    }
}
