package com.xlaoy.nmhgateway.filter;

import com.xlaoy.nmhgateway.support.ApiAccessDecisionManager;
import com.xlaoy.nmhgateway.support.ApiInvocationSecurityMetadataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2018/6/29 0029.
 */
@Component
public class ApiSecurityInterceptorFilter extends AbstractSecurityInterceptor implements Filter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApiInvocationSecurityMetadataSource apiInvocationSecurityMetadataSource;

    /**
     * 注入AccessDecisionManager
     * @param apiAccessDecisionManager
     */
    @Autowired
    public void setApiAccessDecisionManager(ApiAccessDecisionManager apiAccessDecisionManager) {
        super.setAccessDecisionManager(apiAccessDecisionManager);
    }

    /**
     * 设置SecurityMetadataSource
     * @return
     */
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return apiInvocationSecurityMetadataSource;
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        FilterInvocation filterInvocation = new FilterInvocation(httpServletRequest, httpServletResponse, chain);
        if("OPTIONS".equals(httpServletRequest.getMethod())) {
            logger.info("options预检请求");
            chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            logger.info("开始验证资源，url={}", httpServletRequest.getRequestURI());
            InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
            try {
                filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            } finally {
                super.afterInvocation(token, null);
            }
        }
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

}
