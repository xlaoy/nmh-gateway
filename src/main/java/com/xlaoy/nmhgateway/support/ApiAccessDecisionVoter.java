package com.xlaoy.nmhgateway.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by Administrator on 2018/7/13 0013.
 */
@Component
public class ApiAccessDecisionVoter implements AccessDecisionVoter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection collection) {
        return 0;
    }
}
