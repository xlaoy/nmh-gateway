package com.xlaoy.nmhgateway.provider.impl;

import com.xlaoy.nmhgateway.provider.UserRolesProvider;
import com.xlaoy.nmhgateway.support.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Administrator on 2018/12/19 0019.
 */
@Component
public class InMemoryUserRolesProvider implements UserRolesProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Collection<GrantedAuthority> getUserGrantedAuthority(LoginUser loginUser) {
        String roles = "ROLE_NONE";

        logger.info("用户信息：guid={},roles={}", loginUser.getGuid(), roles);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if(!StringUtils.isEmpty(roles)) {
            String[] rolesArray = roles.split(",");
            for(String role : rolesArray) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                authorities.add(authority);
            }
        }

        return authorities;
    }
}
