package com.xlaoy.nmhgateway.config;

import com.xlaoy.nmhgateway.filter.JwtTokenFilter;
import com.xlaoy.nmhgateway.support.ApiAccessDecisionManager;
import com.xlaoy.nmhgateway.support.ApiAccessDeniedHandler;
import com.xlaoy.nmhgateway.support.ApiInvocationSecurityMetadataSource;
import com.xlaoy.nmhgateway.support.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by xlaoy on 2016/11/3.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private ApiAccessDeniedHandler apiAccessDeniedHandler;
    @Autowired
    private ApiInvocationSecurityMetadataSource apiInvocationSecurityMetadataSource;
    @Autowired
    private ApiAccessDecisionManager apiAccessDecisionManager;

    @Override
    public void init(WebSecurity web) throws Exception {
        final HttpSecurity http = this.getHttp();
        web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
            public void run() {
                FilterSecurityInterceptor securityInterceptor = (FilterSecurityInterceptor)http.getSharedObject(FilterSecurityInterceptor.class);
                securityInterceptor.setSecurityMetadataSource(apiInvocationSecurityMetadataSource);
                securityInterceptor.setAccessDecisionManager(apiAccessDecisionManager);
                web.securityInterceptor(securityInterceptor);
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(apiAccessDeniedHandler);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS)
            .antMatchers(
                "/actuator/**",
                "/error",
                "/favicon.ico"
            );
    }

}
