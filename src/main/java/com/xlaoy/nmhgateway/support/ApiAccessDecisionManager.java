package com.xlaoy.nmhgateway.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Administrator on 2018/6/29 0029.
 */
@Component
public class ApiAccessDecisionManager implements AccessDecisionManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        //如果请求的资源没有找到权限则放行，表示该资源为公共资源，都可以访问
        if(CollectionUtils.isEmpty(collection)) {
            return;
        }
        Iterator<ConfigAttribute> iterator = collection.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute attribute = iterator.next();
            String role = attribute.getAttribute();
            for(GrantedAuthority authority : authentication.getAuthorities()) {
                if(role.equals(authority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("没有权限！");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
