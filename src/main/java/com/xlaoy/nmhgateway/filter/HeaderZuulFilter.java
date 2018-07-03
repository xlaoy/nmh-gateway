package com.xlaoy.nmhgateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xlaoy.common.config.ApiBasicAuthProperties;
import com.xlaoy.common.config.SSOConstants;
import com.xlaoy.common.utils.JSONUtil;
import com.xlaoy.nmhgateway.support.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by xlaoy on 2017/8/11.
 */
@Component
@EnableConfigurationProperties(ApiBasicAuthProperties.class)
public class HeaderZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApiBasicAuthProperties basicAuthProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Object object = authentication.getDetails();
            if(object instanceof LoginUser) {
                LoginUser loginUser = (LoginUser)object;
                context.addZuulRequestHeader(SSOConstants.GUID, loginUser.getGuid());
            }
        }

        String serviceId = (String)context.get("serviceId");
        ApiBasicAuthProperties.BasicAuthInfo basicAuthInfo = basicAuthProperties.getServices().get(serviceId);
        String authToken = this.getAuthToken(basicAuthInfo);

        context.addZuulRequestHeader("Authorization", "Basic " + authToken);

        return null;
    }

    private String getAuthToken(ApiBasicAuthProperties.BasicAuthInfo basicAuthInfo) {
        try {
            logger.info("basicAuthInfo={}", JSONUtil.toJsonString(basicAuthInfo));
            byte[] bytes = (basicAuthInfo.getUsername() + ":" + basicAuthInfo.getPassword()).getBytes(StandardCharsets.UTF_8);
            String authToken = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
            logger.info("authToken={}", JSONUtil.toJsonString(authToken));
            return authToken;
        }catch (Exception e) {
            throw new RuntimeException("Basic Auth 配置错误");
        }
    }

}
