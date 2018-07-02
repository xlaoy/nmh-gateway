package com.xlaoy.nmhgateway.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xlaoy.common.config.SSOConstants;
import com.xlaoy.common.support.JsonResponseWriter;
import com.xlaoy.nmhgateway.exception.UserChangeException;
import com.xlaoy.nmhgateway.exception.UserDisableException;
import com.xlaoy.nmhgateway.exception.UserNotFoundException;
import com.xlaoy.nmhgateway.support.JwtAuthenticationToken;
import com.xlaoy.nmhgateway.support.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiredMinute}")
    private Integer expiredMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Exception exception = null;
        try {
            setSecurityUser(request);
        } catch (Exception e) {
            if(e instanceof ExpiredJwtException) {
                logger.error("设置SecurityUser异常：{}", e.getMessage());
            } else {
                logger.error("设置SecurityUser异常", e);
            }
            exception = e;
        }
        if(exception instanceof UserNotFoundException
                || exception instanceof UserDisableException) {
            JsonResponseWriter.response(response)
                    .status(HttpStatus.BAD_REQUEST)
                    .message("用户异常").print();
        } else if(exception instanceof UserChangeException) {
            JsonResponseWriter.response(response)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("用户需要重新登陆").print();
        } else {
            chain.doFilter(request, response);
        }
    }

    private void setSecurityUser(HttpServletRequest request) {
        String token = request.getHeader(SSOConstants.JWT_TOKEN);
        logger.info("请求头信息：jwttoken={}", token);
        if(!StringUtils.isEmpty(token) && !"null".equals(token) && !"undefined".equals(token)) {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            String guid = claims.get(SSOConstants.GUID, String.class);
            //
            this.checkUser(guid);

            String roles = claims.get(SSOConstants.ROLES, String.class);

            logger.info("用户信息：guid={},roles={}", guid, roles);

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if(!StringUtils.isEmpty(roles)) {
                String[] rolesArray = roles.split(",");
                for(String role : rolesArray) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    authorities.add(authority);
                }
            }

            LoginUser loginUser = new LoginUser();
            loginUser.setGuid(guid);
            loginUser.setAuthorities(authorities);
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(guid, "xlaoy-user", authorities);
            jwtAuthenticationToken.setDetails(loginUser);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        }
    }

    /**
     * 在redis里面查找用户，查看：
     * 是否存在
     * 是否被禁用
     * 是否需要重新登陆
     * @param guid
     */
    private void checkUser(String guid) {
        //throw new UserNotFoundException("用户没查到");
        //throw new UserDisableException("用户已禁用");
        //throw new UserChangeException("用户信息有变化，需要重新登陆");
    }
}