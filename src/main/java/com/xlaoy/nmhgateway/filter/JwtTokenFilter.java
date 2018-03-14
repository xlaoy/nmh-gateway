package com.xlaoy.nmhgateway.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xlaoy.nmhgateway.support.JwtAuthenticationToken;
import com.xlaoy.nmhgateway.support.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.jwtheader}")
    private String jwtheader;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiredMinute}")
    private Integer expiredMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String token = request.getHeader(jwtheader);

        if(!StringUtils.isEmpty(token)) {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            String guid = claims.get("guid", String.class);
            String roles = claims.get("roles", String.class);

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            String[] rolesArray = roles.split(",");
            for(String role : rolesArray) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                authorities.add(authority);
            }

            LoginUser loginUser = new LoginUser();
            loginUser.setGuid(guid);
            loginUser.setAuthorities(authorities);
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(guid, "thdc", authorities);
            jwtAuthenticationToken.setDetails(loginUser);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        }

        chain.doFilter(request, response);
    }
}