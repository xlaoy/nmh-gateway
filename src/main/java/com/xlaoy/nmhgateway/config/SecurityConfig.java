package com.xlaoy.nmhgateway.config;

import com.xlaoy.nmhgateway.filter.JwtTokenFilter;
import com.xlaoy.nmhgateway.support.ApiAccessDeniedHandler;
import com.xlaoy.nmhgateway.support.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            //.antMatchers("/api-trade/**").hasRole("SHOP")
            //.antMatchers("/api-user/**").hasRole("SHOP")
            //.anyRequest().authenticated()
            .antMatchers("/**").permitAll()
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
