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
import com.xlaoy.nmhgateway.exception.UserNotFoundException;
import com.xlaoy.nmhgateway.support.JwtAuthenticationToken;
import com.xlaoy.nmhgateway.support.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private UserDetailsChecker userChecker;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Exception exception = null;
        try {
            setSecurityUser(request);
        } catch (Exception e) {
            if(e instanceof ExpiredJwtException) {
                logger.warn("jwttoken过期：{}", e.getMessage());
            } else {
                logger.error("设置SecurityUser异常", e);
            }
            exception = e;
        }
        if(exception instanceof UserNotFoundException
                || exception instanceof DisabledException
                || exception instanceof LockedException) {
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

            LoginUser loginUser = new LoginUser();
            loginUser.setGuid(guid);
            //
            userChecker.check(loginUser);

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
            loginUser.setAuthorities(authorities);
            //
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(guid, "xlaoy-user", authorities);
            jwtAuthenticationToken.setDetails(loginUser);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        }
    }

}