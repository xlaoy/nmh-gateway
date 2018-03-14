package com.xlaoy.nmhgateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xlaoy.nmhgateway.support.ApiBasicAuthProperties;
import com.xlaoy.nmhgateway.support.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class HeaderFilter extends ZuulFilter {

    @Autowired
    private ApiBasicAuthProperties basicAuthProperties;

    @Override
    public String filterType() {
        return "routing";
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
            LoginUser loginUser = (LoginUser)authentication.getDetails();
            context.addZuulRequestHeader("guid", loginUser.getGuid());
        }

        String serviceId = (String)context.get("serviceId");
        ApiBasicAuthProperties.BasicAuthInfo basicAuthInfo = basicAuthProperties.getServices().get(serviceId);
        String authToken;
        try {
            byte[] bytes = (basicAuthInfo.getUsername() + ":" + basicAuthInfo.getPassword()).getBytes(StandardCharsets.UTF_8);
            authToken = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
        }catch (Exception e) {
            throw new RuntimeException("Basic Auth 配置错误");
        }
        context.addZuulRequestHeader("Authorization", "Basic " + authToken);

        return null;
    }

}
