package com.xlaoy.nmhgateway.support;

import com.xlaoy.common.exception.BizException;
import com.xlaoy.nmhgateway.config.RefreshValue;
import com.xlaoy.nmhgateway.provider.impl.DatabaseConfigAttributeProvider;
import com.xlaoy.nmhgateway.provider.impl.InMemoryConfigAttributeProvider;
import com.xlaoy.nmhgateway.provider.impl.MongoConfigAttributeProvider;
import com.xlaoy.nmhgateway.provider.impl.RedisConfigAttributeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by Administrator on 2018/6/29 0029.
 */
@Component
public class ApiInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private InMemoryConfigAttributeProvider inMemoryConfigAttributeProvider;
    @Autowired
    private DatabaseConfigAttributeProvider databaseConfigAttributeProvider;
    @Autowired
    private RedisConfigAttributeProvider redisConfigAttributeProvider;
    @Autowired
    private MongoConfigAttributeProvider mongoConfigAttributeProvider;
    @Autowired
    private RefreshValue refreshValue;

    /**
     * 此方法是为了判定用户请求的url 是否在权限表中，
     * 如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。
     * 如果不在权限表中则放行。
     * @param o
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        FilterInvocation invocation = (FilterInvocation)o;
        String url = invocation.getHttpRequest().getRequestURI();
        Collection<ConfigAttribute> attributeSet = new HashSet<>();
        Map<String, Collection<ConfigAttribute>> map = this.getConfigAttributeMap();
        if(!CollectionUtils.isEmpty(map)) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String resource = iterator.next();
                if(pathMatcher.match(resource, url)) {
                    attributeSet.addAll((HashSet)map.get(resource));
                }
            }
        } else {
            logger.warn("系统资源权限集合为空！");
        }
        return attributeSet;
    }

    /**
     * 从redis中获取每个url对应的权限集合
     * @return
     */
    private Map<String, Collection<ConfigAttribute>> getConfigAttributeMap() {
        Map<String, Collection<ConfigAttribute>> map = new HashMap<>();
        String providerType = refreshValue.getProviderType();
        if("inmemory".equals(providerType)) {
            map = inMemoryConfigAttributeProvider.getConfigAttributeMap();
        } else if("database".equals(providerType)) {
            map = databaseConfigAttributeProvider.getConfigAttributeMap();
        } else if("redis".equals(providerType)) {
            map = redisConfigAttributeProvider.getConfigAttributeMap();
        } else if("mongo".equals(providerType)) {
            map = mongoConfigAttributeProvider.getConfigAttributeMap();
        } else {
            logger.error("gitrep.configattribute.providertype的值只能为[" +
                    "inmemory,database,redis,mongo]中的一个");
            throw new BizException("ConfigAttributeProvider配置类型错误！");
        }
        return map;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
