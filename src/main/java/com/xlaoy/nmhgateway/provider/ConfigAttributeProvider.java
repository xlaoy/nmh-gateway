package com.xlaoy.nmhgateway.provider;

import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/1 0001.
 */
public interface ConfigAttributeProvider {

    Map<String, Collection<ConfigAttribute>> getConfigAttributeMap();
}
