package com.xlaoy.nmhgateway.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xlaoy.common.constants.SSOConstants;
import com.xlaoy.common.support.JsonResponseWriter;
import com.xlaoy.common.utils.JSONUtil;
import com.xlaoy.common.utils.Java8TimeUtil;
import com.xlaoy.nmhgateway.exception.UserChangeException;
import com.xlaoy.nmhgateway.exception.UserNotFoundException;
import com.xlaoy.nmhgateway.kafka.KafkaTopic;
import com.xlaoy.nmhgateway.kafka.RequestURLMessage;
import com.xlaoy.nmhgateway.provider.UserRolesProvider;
import com.xlaoy.nmhgateway.support.JwtAuthenticationToken;
import com.xlaoy.nmhgateway.support.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private UserDetailsChecker userChecker;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private UserRolesProvider userRolesProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Exception exception = null;
        String guid = "none";
        try {
            guid = setSecurityUser(request);
        } catch (Exception e) {
            if(e instanceof ExpiredJwtException) {
                logger.warn("jwttoken过期：{}", e.getMessage());
            } else {
                logger.error("设置SecurityUser异常", e);
            }
            exception = e;
        }

        RequestURLMessage kafkaMessage = new RequestURLMessage(request.getRequestURI());
        kafkaMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Java8TimeUtil.YYYY_MM_DD_HH_MM_SS)));
        kafkaMessage.setGuid(guid);
        this.sendMessage(kafkaMessage);

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

    private String setSecurityUser(HttpServletRequest request) {
        String token = request.getHeader(SSOConstants.JWT_TOKEN);
        logger.info("请求头信息：jwttoken={}", token);
        String guid = null;
        if(!StringUtils.isEmpty(token) && !"null".equals(token) && !"undefined".equals(token)) {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            guid = claims.get(SSOConstants.GUID, String.class);

            LoginUser loginUser = new LoginUser();
            loginUser.setGuid(guid);
            //
            userChecker.check(loginUser);
            //
            Collection<GrantedAuthority> authorities = userRolesProvider.getUserGrantedAuthority(loginUser);
            loginUser.setAuthorities(authorities);
            //
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(guid, "xlaoy-user", authorities);
            jwtAuthenticationToken.setDetails(loginUser);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        }
        return guid;
    }

    private void sendMessage(RequestURLMessage kafkaMessage) {
        try {
            ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(KafkaTopic.REQUEST_URL,
                    //this.getPartitioner(kafkaMessage.getUrl()),
                    kafkaMessage.getGuid(),
                    JSONUtil.toJsonString(kafkaMessage));

            listenableFuture.addCallback(new SuccessCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(@Nullable SendResult<String, String> result) {

                }
            }, new FailureCallback() {
                @Override
                public void onFailure(Throwable ex) {
                    logger.error("", ex);
                }
            });
        } catch (Exception e) {
            logger.error("kafka发送消息异常");
        }
    }

    private Integer getPartitioner(String url) {
        if("/api-trade/".startsWith(url)) {
            return 0;
        }
        if("/api-user/".startsWith(url)) {
            return 1;
        }
        return 0;
    }

}