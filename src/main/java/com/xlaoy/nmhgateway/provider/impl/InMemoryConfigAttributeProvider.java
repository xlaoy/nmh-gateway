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
public class InMemoryConfigAttributeProvider implements ConfigAttributeProvider {

    @Override
    public Map<String, Collection<ConfigAttribute>> getConfigAttributeMap() {
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>();

        Set<ConfigAttribute> set = new HashSet<>();
        set.add(new SecurityConfig("ROLE_ORDINARY_SHOP"));

        map.put("/api-trade/**", set);
        map.put("/api-user/**", set);

        Set<ConfigAttribute> set1 = new HashSet<>();
        set1.add(new SecurityConfig("ROLE_NONE"));
        map.put("/api-user/user/test01", set1);

        return map;
    }
}
