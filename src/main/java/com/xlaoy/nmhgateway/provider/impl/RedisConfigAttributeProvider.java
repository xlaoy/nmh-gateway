package com.xlaoy.nmhgateway.provider.impl;

import com.xlaoy.nmhgateway.provider.ConfigAttributeProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Administrator on 2018/7/1 0001.
 */
@Component
public class RedisConfigAttributeProvider implements ConfigAttributeProvider {

    @Override
    public Map<String, Collection<ConfigAttribute>> getConfigAttributeMap() {
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>();


        return map;
    }
}
