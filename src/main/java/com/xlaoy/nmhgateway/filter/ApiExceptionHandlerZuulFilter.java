package com.xlaoy.nmhgateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xlaoy.common.exception.ExceptionResponse;
import com.xlaoy.common.support.JsonResponseWriter;
import com.xlaoy.common.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2018/6/28 0028.
 */
@Component
public class ApiExceptionHandlerZuulFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        if(HttpStatus.FORBIDDEN.value() == context.getResponseStatusCode()
                || HttpStatus.UNAUTHORIZED.value() == context.getResponseStatusCode()) {
            context.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            exceptionResponse.setMessage("内部系统认证失败");
            context.setResponseBody(JSONUtil.toJsonString(exceptionResponse));
        }

        return null;
    }
}
