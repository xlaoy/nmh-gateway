package com.xlaoy.nmhgateway.provider.impl;

import com.xlaoy.nmhgateway.provider.ConfigAttributeProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/1 0001.
 */
@Component
public class MongoConfigAttributeProvider implements ConfigAttributeProvider {

    @Override
    public Map<String, Collection<ConfigAttribute>> getConfigAttributeMap() {
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>();


        return map;
    }
}
